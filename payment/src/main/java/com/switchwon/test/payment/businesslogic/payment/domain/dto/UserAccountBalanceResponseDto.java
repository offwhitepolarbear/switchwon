package com.switchwon.test.payment.businesslogic.payment.domain.dto;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountBalanceResponseDto {
    // 회원id
    private String userId;

    // 요금
    private BigDecimal balance;

    // 환 종류
    private CurrencyType currency;
    
}
