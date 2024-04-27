package com.switchwon.test.payment.businesslogic.payment.domain.entity;

import com.switchwon.test.payment.global.constants.TableName;
import com.switchwon.test.payment.global.entity.BaseEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = TableName.paymentHistoryFailure)
@Entity
public class PaymentHistoryFailure extends BaseEntity{
    
    @Embedded
    PaymentHistory paymentHistory;
    
    String failureMessage;
}
