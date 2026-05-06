package com.tphone.repository;

import com.tphone.entity.InventoryTransaction;
import com.tphone.enums.InventoryTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findAllByProductIdOrderByCreatedAtDesc(Long productId);

    List<InventoryTransaction> findAllByOrderId(Long orderId);

    List<InventoryTransaction> findAllByCreatedById(Long createdById);

    List<InventoryTransaction> findAllByTransactionType(InventoryTransactionType transactionType);
}