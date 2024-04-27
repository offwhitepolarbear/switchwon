package com.switchwon.test.payment.businesslogic.payment.domain.entity;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.global.entity.BaseEntity;

import jakarta.persistence.Entity;
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
@Entity
public class PaymentEstimateHistory extends BaseEntity{
    private String userId;
    private String merchantId;
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private CurrencyType currency;
}
