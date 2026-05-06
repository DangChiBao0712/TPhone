package com.tphone.service;

import com.tphone.dto.request.CreateReviewRequest;
import com.tphone.dto.request.UpdateReviewRequest;
import com.tphone.dto.request.UpdateReviewStatusRequest;
import com.tphone.dto.response.ReviewResponse;

import java.util.List;

public interface    ReviewService {
    ReviewResponse createReview(Long accountId, CreateReviewRequest request);
    ReviewResponse updateMyReview(Long accountId, Long reviewId, UpdateReviewRequest request);
    void deleteMyReview(Long accountId, Long reviewId);

    List<ReviewResponse> getApprovedReviewsByProduct(Long productId);
    List<ReviewResponse> getMyReviews(Long accountId);

    List<ReviewResponse> getAllReviews();
    ReviewResponse updateReviewStatus(Long reviewId, UpdateReviewStatusRequest request);
}