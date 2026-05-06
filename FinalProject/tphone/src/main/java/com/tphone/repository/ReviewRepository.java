package com.tphone.repository;

import com.tphone.entity.Review;
import com.tphone.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByAccountIdAndProductId(Long accountId, Long productId);

    List<Review> findAllByProductIdAndDeletedAtIsNull(Long productId);

    List<Review> findAllByProductIdAndStatusAndDeletedAtIsNull(Long productId, ReviewStatus status);

    List<Review> findAllByAccountIdAndDeletedAtIsNull(Long accountId);

    Optional<Review> findByIdAndDeletedAtIsNull(Long id);
}