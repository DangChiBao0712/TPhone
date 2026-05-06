package com.tphone.repository;

import com.tphone.entity.OrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @EntityGraph(attributePaths = {"product", "order"})
    List<OrderItem> findAllByOrderId(Long orderId);

    @EntityGraph(attributePaths = {"product", "order"})
    List<OrderItem> findAllByProductId(Long productId);

    void deleteAllByOrderId(Long orderId);
}