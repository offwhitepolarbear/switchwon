package com.switchwon.test.payment.businesslogic.payment.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.UserAccountBalanceResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.PaymentEstimateHistory;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.PaymentHistory;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.UserAccount;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentStatus;
import com.switchwon.test.payment.businesslogic.payment.repository.PaymentEstimateHistoryRepository;
import com.switchwon.test.payment.businesslogic.payment.repository.PaymentHistoryRepository;
import com.switchwon.test.payment.global.constants.BusinessLogicErrorMessagePayment;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService{
    private final PaymentEstimateHistoryRepository paymentEstimateHistoryRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    
    private final UserAccountService userBalanceService;
    private final BigDecimal fee = BigDecimal.valueOf(0.03);
    
    @Override
    public PaymentEstimateResponseDto getPaymentEstimate(PaymentEstimateRequestDto paymentEstimateRequestDto) {
        // 결제 예상 시도 내역을 db 저장
        savePaymentEstimateHistory(paymentEstimateRequestDto);
        
        // 결제 예상 내역 출력
        BigDecimal amount = paymentEstimateRequestDto.getAmount();
        BigDecimal totalAmount = calculatePaymentTotalAmount(amount);
        BigDecimal fee = calculatePaymentFee(amount);

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
        
        String paymentId = generateRandomPaymentId();
        
        PaymentHistory paymentHistory = null;
        
        // 결제 요청에 대한 검사
        validationPaymentApprovalRequest(paymentApprovalRequestDto);
        
        // 결제 금액 차감 반영
        UserAccount userAccount = deductUserBalance(paymentApprovalRequestDto);
        
        // 잔액 검사
        isUserBalanceBelowZero(userAccount);
        
        // 결제 내역 남기기
        paymentHistory = savPaymentHistory(paymentId, paymentApprovalRequestDto);
            
        if (paymentHistory == null){
            throw new RuntimeException(BusinessLogicErrorMessagePayment.paymentFailed);
        }

        // 응답 생성
        PaymentApprovalResponseDto paymentApprovalResponseDto = PaymentApprovalResponseDto.builder()
                                                                .paymentId(paymentId)
                                                                .status(paymentHistory.getPaymentStatus())
                                                                .amountTotal(paymentHistory.getAmountTotal())
                                                                .currency(paymentHistory.getCurrencyType())
                                                                .timestamp(timeStampByLocalDateTime(paymentHistory.getCreatedDateTime()))
                                                                .build();
        return paymentApprovalResponseDto;
    }

    
    private UserAccount deductUserBalance(PaymentApprovalRequestDto paymentApprovalRequestDto){
        String userId = paymentApprovalRequestDto.getUserId();
        BigDecimal totalAmountWithFee = calculatePaymentTotalAmount(paymentApprovalRequestDto.getAmount());
        return userBalanceService.deductFromUserBalance(userId, totalAmountWithFee);
    }

    private PaymentHistory savPaymentHistory(String paymentId, PaymentApprovalRequestDto paymentApprovalRequestDto){
        
        PaymentHistory paymentHistory = PaymentHistory.builder()
        .paymentId(paymentId)
        .paymentStatus(PaymentStatus.approved)
        .paymentMethod(paymentApprovalRequestDto.getPaymentMethod())
        .merchantId(paymentApprovalRequestDto.getMerchantId())
        .amount(paymentApprovalRequestDto.getAmount())
        .amountTotal(calculatePaymentTotalAmount(paymentApprovalRequestDto.getAmount()))
        .currencyType(paymentApprovalRequestDto.getCurrency())
        .cardNumber(paymentApprovalRequestDto.getPaymentDetails().getCardNumber())
        .expiryDate(paymentApprovalRequestDto.getPaymentDetails().getExpiryDate())
        .cvv(paymentApprovalRequestDto.getPaymentDetails().getCvv())
        .build();
        return paymentHistoryRepository.save(paymentHistory);
    }

    private BigDecimal calculatePaymentTotalAmount(BigDecimal amount){
        BigDecimal includingFee = BigDecimal.valueOf(1).add(fee);
        return amount.multiply(includingFee);
    }

    private BigDecimal calculatePaymentFee(BigDecimal amount){
        return amount.multiply(fee);
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
        BigDecimal totalAmount = calculatePaymentTotalAmount(amount);

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


