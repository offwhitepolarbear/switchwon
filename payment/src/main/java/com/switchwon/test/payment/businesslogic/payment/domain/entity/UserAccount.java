package com.switchwon.test.payment.businesslogic.payment.domain.entity;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.global.constants.TableName;
import com.switchwon.test.payment.global.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
@Table(name = TableName.paymentUserAccount)
public class UserAccount extends BaseEntity{
    
    private String userId;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;
    
    // @Builder
    // public UserAccount(String userId, BigDecimal balance, CurrencyType currencyType){
    //     this.userId = userId;
    //     this.balance = balance;
    //     this.currencyType = currencyType;
    // }
}