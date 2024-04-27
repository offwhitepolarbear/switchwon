package com.switchwon.test.payment.businesslogic.payment.domain.dto;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentEstimateResponseDto {
    private BigDecimal estimatedTotal;
    private BigDecimal fees;
    private CurrencyType currency;
    
    @Builder
    public PaymentEstimateResponseDto(BigDecimal estimatedTotal, BigDecimal fees, CurrencyType currency){
        this.estimatedTotal = estimatedTotal;
        this.fees = fees;
        this.currency = currency;
    }
}
