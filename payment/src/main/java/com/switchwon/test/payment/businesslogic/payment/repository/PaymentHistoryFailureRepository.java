package com.switchwon.test.payment.businesslogic.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.switchwon.test.payment.businesslogic.payment.domain.entity.PaymentHistoryFailure;

@Repository
public interface PaymentHistoryFailureRepository extends JpaRepository<PaymentHistoryFailure, UUID>{

}
