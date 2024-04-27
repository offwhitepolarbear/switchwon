package com.switchwon.test.payment.service;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.UserAccountBalanceResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.UserAccount;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.businesslogic.payment.service.UserAccountService;

@SpringBootTest
public class UserAccountServiceTest {

    @Autowired
    private UserAccountService userAccountService;
    
    @DisplayName("잔액 조회 테스트")
    @Test
    public void getUserAccountBalanceTest(){
        // 테스트 값 설정
        UserAccount testUseraccount = createTestUserAccount();
        userAccountService.createInitialUserBalance(testUseraccount);
        
        // 기능 실행
        UserAccountBalanceResponseDto userAccountBalanceResponseDto = userAccountService.getBalanceByUserId(testUseraccount.getUserId());
        BigDecimal balance = userAccountBalanceResponseDto.getBalance();
        CurrencyType currencyType = userAccountBalanceResponseDto.getCurrency();

        // 잔액과 환타입 검사
        Assertions.assertThat(testUseraccount.getCurrencyType()).isEqualTo(currencyType);
        Assertions.assertThat(testUseraccount.getBalance()).isEqualByComparingTo(balance);

    }

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


}
