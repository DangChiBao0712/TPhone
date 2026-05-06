package com.tphone.controller.client;

import com.tphone.dto.request.CreateReviewRequest;
import com.tphone.dto.request.UpdateReviewRequest;
import com.tphone.dto.response.MessageResponse;
import com.tphone.dto.response.ReviewResponse;
import com.tphone.security.model.CustomUserDetails;
import com.tphone.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<ReviewResponse> getApprovedReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getApprovedReviewsByProduct(productId);
    }

    @GetMapping("/my")
    public List<ReviewResponse> getMyReviews(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return reviewService.getMyReviews(userDetails.getAccountId());
    }

    @PostMapping
    public ReviewResponse createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreateReviewRequest request
    ) {
        return reviewService.createReview(userDetails.getAccountId(), request);
    }

    @PutMapping("/{reviewId}")
    public ReviewResponse updateMyReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewRequest request
    ) {
        return reviewService.updateMyReview(userDetails.getAccountId(), reviewId, request);
    }

    @DeleteMapping("/{reviewId}")
    public MessageResponse deleteMyReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteMyReview(userDetails.getAccountId(), reviewId);
        return new MessageResponse("Review deleted successfully");
    }
}