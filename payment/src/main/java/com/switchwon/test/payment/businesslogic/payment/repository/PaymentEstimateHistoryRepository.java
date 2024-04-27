package com.switchwon.test.payment.businesslogic.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.switchwon.test.payment.businesslogic.payment.domain.entity.PaymentEstimateHistory;

@Repository
public interface PaymentEstimateHistoryRepository extends JpaRepository<PaymentEstimateHistory, UUID>{

}
