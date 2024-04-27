package com.switchwon.test.payment.businesslogic.payment.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.switchwon.test.payment.businesslogic.payment.domain.dto.UserAccountBalanceResponseDto;
import com.switchwon.test.payment.businesslogic.payment.domain.entity.UserAccount;
import com.switchwon.test.payment.businesslogic.payment.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAccountServiceImpl implements UserAccountService{

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccount createInitialUserBalance(UserAccount userAccount) {
        return userAccountRepository.save(userAccount);
    }

    @Override
    public UserAccount getUserAccountByUserId(String userId) {
        UserAccount userAccount = null;
        Optional<UserAccount> userAccountOptional = userAccountRepository.findByUserId(userId);
        if(userAccountOptional.isPresent()){
            userAccount = userAccountOptional.get();
        }
        return userAccount;
    }

    @Override
    public UserAccountBalanceResponseDto getBalanceByUserId(String userId) {
        UserAccountBalanceResponseDto userBalanceResponseDto = null;
        Optional<UserAccount> optionalUserBalance = userAccountRepository.findByUserId(userId);
        if (optionalUserBalance.isPresent()){
            UserAccount userAccount = optionalUserBalance.get();

            userBalanceResponseDto = UserAccountBalanceResponseDto.builder()
            .userId(userAccount.getUserId())
            .balance(userAccount.getBalance())
            .currency(userAccount.getCurrencyType())
            .build();
    
        }
        return userBalanceResponseDto;
    }

    @Override
    public UserAccount deductFromUserBalance(String userId, BigDecimal totalAmount) {
        UserAccount userBalance = null;
        
        Optional<UserAccount> optionalUserBalance = userAccountRepository.findByUserId(userId);
        
        if (optionalUserBalance.isPresent()){
            userBalance = optionalUserBalance.get();
            BigDecimal userBalanceAmount = userBalance.getBalance();
            BigDecimal deductedBalance = userBalanceAmount.subtract(totalAmount);
            userBalance.setBalance(deductedBalance);
        }
        
        return userBalance;
    }

}
