package com.switchwon.test.payment.businesslogic.payment.domain.dto;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentStatus;

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
public class PaymentApprovalResponseDto {
    private String paymentId;
    private PaymentStatus status;
    private BigDecimal amountTotal;
    private CurrencyType currency;
    private String timestamp; 

}
