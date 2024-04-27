package com.switchwon.test.payment.businesslogic.payment.domain.dto;

import com.switchwon.test.payment.global.constants.RegexErrorMessagesPayment;
import com.switchwon.test.payment.global.constants.RegexPatternPayment;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
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
@Embeddable
public class PaymentDetails {
    
    @NotNull
    @Pattern(regexp = RegexPatternPayment.cardNumberPattern, message = RegexErrorMessagesPayment.cardNumberMessage)
    private String cardNumber;

    @NotNull
    @Pattern(regexp = RegexPatternPayment.expiryDatePattern, message = RegexErrorMessagesPayment.expiryDateMessage)
    private String expiryDate;
    
    @NotNull
    @Pattern(regexp = RegexPatternPayment.cvvPattern, message = RegexErrorMessagesPayment.cvvMessage)
    private String cvv;
}
