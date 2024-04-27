
package com.switchwon.test.payment.global.constants;

public class UrlMappingConstants {
    private static final String apiPrefix = "/api";
    
    private static final String paymentPath = "/payment";
    private static final String userAccountPath = "/userAccount";

    public static final String paymentGetBalance = apiPrefix + paymentPath + "/balance/{userId}";
    public static final String paymentPostEstimate = apiPrefix + paymentPath + "/estimate";
    public static final String paymentPostApporoval = apiPrefix + paymentPath + "/approval";
    
    public static final String userAccountPostCreate = apiPrefix + userAccountPath + "/create";

}
