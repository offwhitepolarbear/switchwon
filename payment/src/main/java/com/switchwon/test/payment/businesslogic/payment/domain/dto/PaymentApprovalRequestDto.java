package com.switchwon.test.payment.businesslogic.payment.domain.dto;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentMethod;

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
public class PaymentApprovalRequestDto {
    private String userId;
    private String merchantId;
    private BigDecimal amount;
    private CurrencyType currency;
    private PaymentMethod paymentMethod;
    private PaymentDetails paymentDetails;

}  
