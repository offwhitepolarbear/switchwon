package com.switchwon.test.payment.businesslogic.payment.domain.dto;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentEstimateRequestDto {
    private String userId;
    private String merchantId;
    private BigDecimal amount;
    private CurrencyType currency;

    @Builder
    public PaymentEstimateRequestDto(String userId, String merchantId, BigDecimal amount, CurrencyType currency){
        this.userId = userId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
    }
}
