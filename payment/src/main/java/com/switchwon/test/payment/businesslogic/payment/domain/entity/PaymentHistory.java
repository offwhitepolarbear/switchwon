package com.switchwon.test.payment.businesslogic.payment.domain.entity;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentDetails;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentMethod;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentStatus;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Embeddable
public class PaymentHistory{

    private String paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    
    private String userId;
    private String merchantId;
    private BigDecimal amount;
    private BigDecimal amountTotal;
    
    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @Embedded
    PaymentDetails paymentDetails;

}
