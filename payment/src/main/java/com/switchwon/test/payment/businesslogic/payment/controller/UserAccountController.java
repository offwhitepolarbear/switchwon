package com.switchwon.test.payment.businesslogic.payment.controller;

import org.springframework.web.bind.annotation.RestController;

import com.switchwon.test.payment.businesslogic.payment.domain.entity.UserAccount;
import com.switchwon.test.payment.businesslogic.payment.service.UserAccountService;
import com.switchwon.test.payment.global.constants.UrlMappingConstants;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping(UrlMappingConstants.userAccountPostCreate)
    public ResponseEntity<UserAccount> postMethodName(@RequestBody UserAccount userAccount) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        UserAccount userAccountResult = userAccountService.createInitialUserBalance(userAccount);
        return new ResponseEntity<>(userAccountResult, httpStatus);
    }
    
}
