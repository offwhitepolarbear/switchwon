package com.switchwon.test.payment.businesslogic.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentDetails;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.UserAccountBalanceResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.PaymentEstimateHistory;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.PaymentHistory;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.PaymentHistoryFailure;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.PaymentHistorySuccess;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.UserAccount;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentStatus;
import com.switchwon.test.payment.businesslogic.payment.repository.PaymentEstimateHistoryRepository;
import com.switchwon.test.payment.businesslogic.payment.repository.PaymentHistoryFailureRepository;
import com.switchwon.test.payment.businesslogic.payment.repository.PaymentHistorySuccessRepository;
import com.switchwon.test.payment.global.constants.BusinessLogicErrorMessagePayment;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService{
    private final PaymentEstimateHistoryRepository paymentEstimateHistoryRepository;
    private final PaymentHistorySuccessRepository paymentHistorySuccessRepository;
    private final PaymentHistoryFailureRepository paymentHistoryFailureRepository;

    private final UserAccountService userBalanceService;
    private final BigDecimal fee = BigDecimal.valueOf(0.03);
    
    @Override
    public PaymentEstimateResponseDto getPaymentEstimate(PaymentEstimateRequestDto paymentEstimateRequestDto) {
        // 결제 예상 시도 내역을 db 저장
        savePaymentEstimateHistory(paymentEstimateRequestDto);
        
        // 결제 예상 내역 출력
        BigDecimal amount = paymentEstimateRequestDto.getAmount();
        BigDecimal totalAmount = calculatePaymentTotalAmount(amount, paymentEstimateRequestDto.getCurrency());
        BigDecimal fee = calculatePaymentFee(amount, paymentEstimateRequestDto.getCurrency());

        PaymentEstimateResponseDto paymentEstimateResponseDto = PaymentEstimateResponseDto.builder()
                                                                .estimatedTotal(totalAmount)
                                                                .fees(fee)
                                                                .currency(paymentEstimateRequestDto.getCurrency())
                                                                .build();
        
        return paymentEstimateResponseDto;
    }

    private PaymentEstimateHistory savePaymentEstimateHistory(PaymentEstimateRequestDto paymentEstimateRequestDto){
        
        PaymentEstimateHistory paymentEstimateHistory = PaymentEstimateHistory.builder()
        .userId(paymentEstimateRequestDto.getUserId())
        .merchantId(paymentEstimateRequestDto.getMerchantId())
        .amount(paymentEstimateRequestDto.getAmount())
        .currency(paymentEstimateRequestDto.getCurrency()).build();

        return paymentEstimateHistoryRepository.save(paymentEstimateHistory);
    }

    @Transactional
    @Override
    public PaymentApprovalResponseDto approvePayment(PaymentApprovalRequestDto paymentApprovalRequestDto) {
        PaymentApprovalResponseDto paymentApprovalResponseDto = null;
        PaymentHistory paymentHistory = generatePaymentHistory(paymentApprovalRequestDto);
        try{
        
            PaymentHistorySuccess paymentHistorySuccess = null;
            
            // 결제 요청에 대한 검사
            validationPaymentApprovalRequest(paymentApprovalRequestDto);
            
            // 결제 금액 차감 반영
            UserAccount userAccount = deductUserBalance(paymentApprovalRequestDto);
            
            // 잔액 검사
            isUserBalanceBelowZero(userAccount);
            
            // 결제 내역 남기기
            paymentHistorySuccess = savPaymentHistorySuccess(paymentHistory);
                
            if (paymentHistorySuccess == null || paymentHistorySuccess.getId() == null){
                throw new RuntimeException(BusinessLogicErrorMessagePayment.paymentFailed);
            }

            // 응답 생성
            paymentApprovalResponseDto = PaymentApprovalResponseDto.builder()
                                                                    .paymentId(paymentHistorySuccess.getPaymentHistory().getPaymentId())
                                                                    .status(paymentHistorySuccess.getPaymentHistory().getPaymentStatus())
                                                                    .amountTotal(paymentHistorySuccess.getPaymentHistory().getAmountTotal())
                                                                    .currency(paymentHistorySuccess.getPaymentHistory().getCurrencyType())
                                                                    .timestamp(timeStampByLocalDateTime(paymentHistorySuccess.getCreatedDateTime()))
                                                                    .build();
        } catch(Exception e){
            savePaymentHistoryFailure(paymentHistory, e.toString());
        }
        
        return paymentApprovalResponseDto;
    }
    
    private UserAccount deductUserBalance(PaymentApprovalRequestDto paymentApprovalRequestDto){
        String userId = paymentApprovalRequestDto.getUserId();
        BigDecimal totalAmountWithFee = calculatePaymentTotalAmount(paymentApprovalRequestDto.getAmount(), paymentApprovalRequestDto.getCurrency());
        return userBalanceService.deductFromUserBalance(userId, totalAmountWithFee);
    }  

    // 결제 내역 초안 작성
    private PaymentHistory generatePaymentHistory(PaymentApprovalRequestDto paymentApprovalRequestDto){
        
        PaymentDetails paymentDetails = PaymentDetails.builder()
        .cardNumber(paymentApprovalRequestDto.getPaymentDetails().getCardNumber())
        .expiryDate(paymentApprovalRequestDto.getPaymentDetails().getExpiryDate())
        .cvv(paymentApprovalRequestDto.getPaymentDetails().getCvv())
        .build();

        String paymentId = generateRandomPaymentId();
        
        return PaymentHistory.builder().paymentId(paymentId)
        .paymentStatus(PaymentStatus.approved)
        .paymentMethod(paymentApprovalRequestDto.getPaymentMethod())
        .merchantId(paymentApprovalRequestDto.getMerchantId())
        .amount(paymentApprovalRequestDto.getAmount())
        .amountTotal(calculatePaymentTotalAmount(paymentApprovalRequestDto.getAmount(), paymentApprovalRequestDto.getCurrency()))
        .currencyType(paymentApprovalRequestDto.getCurrency()).paymentDetails(paymentDetails)
        .build();
    }

    // 결제 성공 내역 저장
    private PaymentHistorySuccess savPaymentHistorySuccess(PaymentHistory paymentHistory){
        PaymentHistorySuccess paymentHistorySuccess = PaymentHistorySuccess.builder().paymentHistory(paymentHistory).build();
        return paymentHistorySuccessRepository.save(paymentHistorySuccess);
    }

    // 결제 오류 내역 저장
    private PaymentHistoryFailure savePaymentHistoryFailure(PaymentHistory paymentHistory, String failureMessage){
        paymentHistory.setPaymentStatus(PaymentStatus.rejected);
        PaymentHistoryFailure paymentHistoryFailure = PaymentHistoryFailure.builder().paymentHistory(paymentHistory).failureMessage(failureMessage).build();
        return paymentHistoryFailureRepository.save(paymentHistoryFailure);
    }


    // 수수료가 포함된 총액 계산
    private BigDecimal calculatePaymentTotalAmount(BigDecimal amount, CurrencyType currencyType){
        BigDecimal includingFee = BigDecimal.valueOf(1).add(fee);
        amount = amount.multiply(includingFee);

        if(currencyType.equals(CurrencyType.KRW)){
            amount = truncateKrwPayment(amount);
        }
        if(currencyType.equals(CurrencyType.USD)){
            amount = truncateUsdPayment(amount);
        }

        return amount;
    }

    // 수수료만 계산
    private BigDecimal calculatePaymentFee(BigDecimal amount, CurrencyType currencyType){
        amount = amount.multiply(fee);

        if(currencyType.equals(CurrencyType.KRW)){
            amount = truncateKrwPayment(amount);
        }

        if(currencyType.equals(CurrencyType.USD)){
            amount = truncateUsdPayment(amount);
        }

        return amount;
    }

    // 한화 소수자리 미만 절삭
    private BigDecimal truncateKrwPayment(BigDecimal bigDecimal){
        return bigDecimal.setScale(0, RoundingMode.DOWN);
    }

    // 달러화 소수2자리 내림 절삭
    private BigDecimal truncateUsdPayment(BigDecimal bigDecimal){
        return bigDecimal.setScale(2, RoundingMode.DOWN);
    }

    // 결제 요청 유효성 검사
    private void validationPaymentApprovalRequest(PaymentApprovalRequestDto paymentApprovalRequestDto){
        
        // userBalance 잔액 유효성 검사
        getEnoughBalance(paymentApprovalRequestDto);
        
        // 카드 유효기간 검사
        String expiryDate = paymentApprovalRequestDto.getPaymentDetails().getExpiryDate();
        isCardExpired(expiryDate);
    }

    private void getEnoughBalance(PaymentApprovalRequestDto paymentApprovalRequestDto) {
        
        String userId = paymentApprovalRequestDto.getUserId();
        UserAccountBalanceResponseDto userBalanceResponseDto = userBalanceService.getBalanceByUserId(userId);
        BigDecimal userBalance = userBalanceResponseDto.getBalance();
        BigDecimal amount = paymentApprovalRequestDto.getAmount();
        BigDecimal totalAmount = calculatePaymentTotalAmount(amount, paymentApprovalRequestDto.getCurrency());

        // 잔액과 결제 금액 bigDecimal 비교 연산 처리
        boolean enoughBalance = isFirstNumberIsGreaterThanOrEqualSecondNumber(userBalance, totalAmount);
        
        if (!enoughBalance){
            throw new ValidationException(BusinessLogicErrorMessagePayment.balanceUnderZero);
        }
    }

    private void isCardExpired(String expiryDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth cardExpiry = YearMonth.parse(expiryDate, formatter);
        YearMonth currentYearMonth = YearMonth.now();
        if (cardExpiry.isBefore(currentYearMonth)){
            throw new ValidationException(BusinessLogicErrorMessagePayment.expiredCard);
        }
    }

    // 결제 이후 잔액 유효성 검사
    private void isUserBalanceBelowZero(UserAccount userBalance) {
        boolean userBalanceBelowZero = userBalance.getBalance().compareTo(BigDecimal.ZERO) < 0;
        if (userBalanceBelowZero){
            throw new ValidationException(BusinessLogicErrorMessagePayment.balanceUnderZero);
        }
    }

    private boolean isFirstNumberIsGreaterThanOrEqualSecondNumber(BigDecimal firstNumber, BigDecimal secondNumber){
        boolean result = false;
        if (firstNumber.compareTo(secondNumber)>=0){
            result = true;
        }
        return result;
    }
    
    // 결제 id 생성기
    private String generateRandomPaymentId() {
        final String char_lower = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String number = "0123456789";        
        String randomPaymentId = generateRandomString(9, char_lower) + generateRandomString(5, number);
        return randomPaymentId;
    }

    private String generateRandomString(int length, String characterSet) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(characterSet.charAt(random.nextInt(characterSet.length())));
        }
        return builder.toString();
    }

    private String timeStampByLocalDateTime(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formatDateTime = localDateTime.format(formatter);
        return formatDateTime;
    }

}


