package com.switchwon.test.payment.businesslogic.payment.service;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateResponseDto;

public interface PaymentService {
    public PaymentEstimateResponseDto getPaymentEstimate(PaymentEstimateRequestDto paymentEstimateRequestDto);
    public PaymentApprovalResponseDto approvePayment(PaymentApprovalRequestDto paymentApprovalRequestDto);
}
