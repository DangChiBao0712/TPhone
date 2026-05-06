package com.tphone.repository;

import com.tphone.entity.Payment;
import com.tphone.enums.PaymentProvider;
import com.tphone.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionCode(String transactionCode);

    Optional<Payment> findByProviderTransactionId(String providerTransactionId);

    List<Payment> findAllByOrderId(Long orderId);

    List<Payment> findAllByStatus(PaymentStatus status);

    List<Payment> findAllByProvider(PaymentProvider provider);
}