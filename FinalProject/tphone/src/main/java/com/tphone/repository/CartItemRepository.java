package com.tphone.repository;

import com.tphone.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = {"product", "cart"})
    List<CartItem> findAllByCartId(Long cartId);

    @EntityGraph(attributePaths = {"product", "cart"})
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @EntityGraph(attributePaths = {"product", "cart"})
    Optional<CartItem> findById(Long id);

    boolean existsByCartIdAndProductId(Long cartId, Long productId);

    void deleteByCartIdAndProductId(Long cartId, Long productId);

    void deleteAllByCartId(Long cartId);
}