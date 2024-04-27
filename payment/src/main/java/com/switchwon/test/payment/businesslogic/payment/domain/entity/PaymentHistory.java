package com.switchwon.test.payment.businesslogic.payment.domain.entity;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.enums.CurrencyType;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentMethod;
import com.switchwon.test.payment.businesslogic.payment.domain.enums.PaymentStatus;
import com.switchwon.test.payment.global.constants.RegexErrorMessagesPayment;
import com.switchwon.test.payment.global.constants.RegexPatternPayment;
import com.switchwon.test.payment.global.constants.TableName;
import com.switchwon.test.payment.global.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
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
@Table(name = TableName.PaymentHistory)
public class PaymentHistory extends BaseEntity{

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

    @Pattern(regexp = RegexPatternPayment.cardNumberPattern, message = RegexErrorMessagesPayment.cardNumberMessage)
    private String cardNumber;

    @Pattern(regexp = RegexPatternPayment.expiryDatePattern, message = RegexErrorMessagesPayment.expiryDateMessage)
    private String expiryDate;
    
    @Pattern(regexp = RegexPatternPayment.cvvPattern, message = RegexErrorMessagesPayment.cvvMessage)
    private String cvv;

}
