package com.switchwon.test.payment.businesslogic.payment.repository;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.switchwon.test.payment.businesslogic.payment.domain.entity.UserAccount;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID>{
    Optional<UserAccount> findByUserId(String userId);
}
