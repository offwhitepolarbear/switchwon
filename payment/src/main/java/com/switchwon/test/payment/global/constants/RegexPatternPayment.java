package com.switchwon.test.payment.global.constants;

public class RegexPatternPayment {
    public static final String cardNumberPattern = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$";
    public static final String expiryDatePattern = "^(0[1-9]|1[0-2])\\/([0-9]{2})$";
    public static final String cvvPattern = "^\\d{3}$";
    
}
