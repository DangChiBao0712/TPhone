package com.tphone.service.impl;

import com.tphone.dto.request.CreateReviewRequest;
import com.tphone.dto.request.UpdateReviewRequest;
import com.tphone.dto.request.UpdateReviewStatusRequest;
import com.tphone.dto.response.ReviewResponse;
import com.tphone.entity.Account;
import com.tphone.entity.Product;
import com.tphone.entity.Review;
import com.tphone.enums.ReviewStatus;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.AccountRepository;
import com.tphone.repository.ProductRepository;
import com.tphone.repository.ReviewRepository;
import com.tphone.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Override
    public ReviewResponse createReview(Long accountId, CreateReviewRequest request) {
        validateRating(request.getRating());

        if (request.getProductId() == null) {
            throw new BadRequestException("Product id is required");
        }

        if (reviewRepository.findByAccountIdAndProductId(accountId, request.getProductId()).isPresent()) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này rồi");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Product product = productRepository.findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Review review = new Review();
        review.setAccount(account);
        review.setProduct(product);
        review.setRating(request.getRating().byteValue());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setStatus(ReviewStatus.PENDING);

        return toResponse(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse updateMyReview(Long accountId, Long reviewId, UpdateReviewRequest request) {
        validateRating(request.getRating());

        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getAccount().getId().equals(accountId)) {
            throw new BadRequestException("Review does not belong to current user");
        }

        review.setRating(request.getRating().byteValue());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setStatus(ReviewStatus.PENDING);

        return toResponse(reviewRepository.save(review));
    }

    @Override
    public void deleteMyReview(Long accountId, Long reviewId) {
        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getAccount().getId().equals(accountId)) {
            throw new BadRequestException("Review does not belong to current user");
        }

        review.setDeletedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Override
    public List<ReviewResponse> getApprovedReviewsByProduct(Long productId) {
        return reviewRepository.findAllByProductIdAndStatusAndDeletedAtIsNull(productId, ReviewStatus.APPROVED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ReviewResponse> getMyReviews(Long accountId) {
        return reviewRepository.findAllByAccountIdAndDeletedAtIsNull(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .filter(review -> review.getDeletedAt() == null)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReviewResponse updateReviewStatus(Long reviewId, UpdateReviewStatusRequest request) {
        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        ReviewStatus status;
        try {
            status = ReviewStatus.valueOf(request.getStatus().toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid review status");
        }

        review.setStatus(status);
        return toResponse(reviewRepository.save(review));
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getAccount() != null ? review.getAccount().getId() : null,
                review.getAccount() != null ? review.getAccount().getFullName() : null,
                review.getProduct() != null ? review.getProduct().getId() : null,
                review.getProduct() != null ? review.getProduct().getName() : null,
                review.getOrder() != null ? review.getOrder().getId() : null,
                review.getRating() != null ? review.getRating().intValue() : null,
                review.getTitle(),
                review.getContent(),
                review.getStatus() != null ? review.getStatus().name() : null
        );
    }
}