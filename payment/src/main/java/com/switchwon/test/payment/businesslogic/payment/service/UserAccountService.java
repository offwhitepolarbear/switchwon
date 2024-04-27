package com.switchwon.test.payment.businesslogic.payment.service;

import java.math.BigDecimal;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.UserAccountBalanceResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.UserAccount;
 
public interface UserAccountService {
    public UserAccount createInitialUserBalance(UserAccount userAccount);
    public UserAccount getUserAccountByUserId(String userId);
    public UserAccountBalanceResponseDto getBalanceByUserId(String userId);
    public UserAccount deductFromUserBalance(String userId, BigDecimal totalAmount);
}
