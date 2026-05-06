package com.tphone.repository;

import com.tphone.entity.Order;
import com.tphone.enums.OrderStatus;
import com.tphone.enums.PaymentMethod;
import com.tphone.enums.PaymentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);

    @EntityGraph(attributePaths = {"account"})
    List<Order> findAllByAccountId(Long accountId);

    @EntityGraph(attributePaths = {"account"})
    List<Order> findAllByAccountIdOrderByCreatedAtDesc(Long accountId);

    List<Order> findAllByOrderStatus(OrderStatus orderStatus);

    List<Order> findAllByPaymentStatus(PaymentStatus paymentStatus);

    List<Order> findAllByPaymentMethod(PaymentMethod paymentMethod);

    Long countByOrderStatus(OrderStatus status);

    Long countByAccountId(Long accountId);

    @Query("""
            select coalesce(sum(o.totalAmount), 0)
            from Order o
            where o.account.id = :accountId
              and o.paymentStatus = com.tphone.enums.PaymentStatus.SUCCESS
            """)
    BigDecimal sumSuccessfulTotalAmountByAccountId(@Param("accountId") Long accountId);
}