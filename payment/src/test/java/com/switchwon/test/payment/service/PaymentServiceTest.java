package com.switchwon.test.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentDetails;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.UserAccount;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentMethod;
import com.switchwon.test.payment.businesslogic.payment.service.PaymentService;
import com.switchwon.test.payment.businesslogic.payment.service.UserAccountService;

@SpringBootTest
public class PaymentServiceTest {
    
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserAccountService userAccountService;

    @DisplayName("결제 금액 예상 테스트")
    @Test
    public void testPaymentEstimate(){
        // 테스트 데이터
        PaymentEstimateRequestDto paymentEstimateRequestDto = createTestRequestDto();

        
        
        // 테스트할 기능
        PaymentEstimateResponseDto paymentEstimateResponseDto = paymentService.getPaymentEstimate(paymentEstimateRequestDto);

        // 절삭된 금액
        BigDecimal fee = paymentEstimateResponseDto.getFees();
        BigDecimal totalAmount = paymentEstimateResponseDto.getEstimatedTotal();
        CurrencyType currency = paymentEstimateResponseDto.getCurrency();
       
        // 수수료 변경시 수정 필요
        BigDecimal feeRate = BigDecimal.valueOf(0.03);
        BigDecimal totalRate = BigDecimal.valueOf(1.03);

        int scale = 0;

        if (currency.equals(CurrencyType.USD)){
            scale = 2;
        }
        // 결과 확인 
        Assertions.assertThat(paymentEstimateRequestDto.getCurrency()).isEqualTo(currency);
        Assertions.assertThat(paymentEstimateRequestDto.getAmount().multiply(feeRate).setScale(scale, RoundingMode.DOWN)).isEqualTo(fee);
        Assertions.assertThat(paymentEstimateRequestDto.getAmount().multiply(totalRate).setScale(scale, RoundingMode.DOWN)).isEqualTo(totalAmount);
    }

    // 더미 요청 생성
    private PaymentEstimateRequestDto createTestRequestDto(){
        String testUserId = "testUser";
        String testMerchantId = "testMerchant";
        BigDecimal paymentAmount = BigDecimal.valueOf(1500.03);
        CurrencyType currencyType = CurrencyType.USD;
        PaymentEstimateRequestDto paymentEstimateRequestDto = PaymentEstimateRequestDto.builder()
        .userId(testUserId)
        .merchantId(testMerchantId)
        .amount(paymentAmount)
        .currency(currencyType)
        .build();

        return paymentEstimateRequestDto;
    }

    @DisplayName("결제 테스트")
    @Test
    public void paymentAppovalTest(){
        // 계좌 잔액 생성
        UserAccount userAccount = createTestUserAccount();
        BigDecimal balanceBeforePayment = userAccount.getBalance();
        userAccount = userAccountService.createInitialUserBalance(userAccount);
 
        // 결제 요청 생성
        PaymentApprovalRequestDto paymentApprovalRequestDto =  createTestPaymentApprovalRequestDto();
        PaymentApprovalResponseDto paymentEstimateResponseDto = paymentService.approvePayment(paymentApprovalRequestDto);
        BigDecimal paymentAmount = paymentEstimateResponseDto.getAmountTotal();

        UserAccount latestUserAccount = userAccountService.getUserAccountByUserId(userAccount.getUserId());
        BigDecimal balanceAfterPayment = latestUserAccount.getBalance();

        Assertions.assertThat(balanceAfterPayment).isEqualByComparingTo(balanceBeforePayment.subtract(paymentAmount));

    }

    // mock 회원 잔액 설정
    private UserAccount createTestUserAccount(){
        String testUserId = "testUser";
        BigDecimal testUserBalance = BigDecimal.valueOf(4321.30);
        CurrencyType testCurrency = CurrencyType.USD;
        return UserAccount.builder()
        .userId(testUserId)
        .balance(testUserBalance)
        .currencyType(testCurrency)
        .build();
    }

    // mock 결제 승인 요청 
    private PaymentApprovalRequestDto createTestPaymentApprovalRequestDto(){
        
        String userId = "testUser";
        String merchatId = "mlmasdf";
        BigDecimal amountBigDecimal = BigDecimal.valueOf(100);
        CurrencyType currencyType = CurrencyType.USD;
        PaymentMethod paymentMethod = PaymentMethod.creditCard;
        String cvv = "000";

        PaymentDetails paymentDetails = PaymentDetails.builder()
        .cardNumber("1234-1234-4444-3333")
        .expiryDate("05/24")
        .cvv(cvv)
        .build();

        PaymentApprovalRequestDto paymentApprovalRequestDto = PaymentApprovalRequestDto.builder()
        .userId(userId)
        .merchantId(merchatId)
        .amount(amountBigDecimal)
        .currency(currencyType)
        .paymentDetails(paymentDetails)
        .paymentMethod(paymentMethod)
        .build();
        
        return paymentApprovalRequestDto;
    }

}
