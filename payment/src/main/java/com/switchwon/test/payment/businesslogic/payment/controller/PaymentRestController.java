package com.switchwon.test.payment.businesslogic.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentApprovalRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateRequestDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.PaymentEstimateResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.dto.UserAccountBalanceResponseDto;
import com.switchwon.test.payment.businesslogic.payment.service.PaymentService;
import com.switchwon.test.payment.businesslogic.payment.service.UserAccountService;
import com.switchwon.test.payment.global.constants.UrlMappingConstants;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
public class PaymentRestController {
    private final UserAccountService userAccountService;
    private final PaymentService paymentService;

    @GetMapping(UrlMappingConstants.paymentGetBalance)
    public ResponseEntity<?> getBalance(@PathVariable String userId){
        HttpStatusCode httpStatusCode = HttpStatus.OK;
        UserAccountBalanceResponseDto userBalanceResponseDto = userAccountService.getBalanceByUserId(userId);
        return new ResponseEntity<>(userBalanceResponseDto, httpStatusCode);
    }

    @PostMapping(UrlMappingConstants.paymentPostEstimate)
    public ResponseEntity<?> paymentEstimate(@RequestBody PaymentEstimateRequestDto paymentEstimateRequestDto){
        HttpStatusCode httpStatusCode = HttpStatus.OK;
        PaymentEstimateResponseDto paymentEstimateResponseDto = paymentService.getPaymentEstimate(paymentEstimateRequestDto);
        return new ResponseEntity<>(paymentEstimateResponseDto, httpStatusCode);
    }

    @PostMapping(UrlMappingConstants.paymentPostApporoval)
    public ResponseEntity<?> paymentApporval(@RequestBody PaymentApprovalRequestDto paymentApprovalRequestDto){
        HttpStatusCode httpStatusCode = HttpStatus.CREATED;
        
        return new ResponseEntity<>(paymentService.approvePayment(paymentApprovalRequestDto), httpStatusCode);
    }

}
