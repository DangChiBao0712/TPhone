package com.tphone.repository;

import com.tphone.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"account"})
    Optional<Cart> findByAccountId(Long accountId);

    boolean existsByAccountId(Long accountId);
}