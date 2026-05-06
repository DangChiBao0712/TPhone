package com.tphone.controller.admin;

import com.tphone.dto.request.UpdateReviewStatusRequest;
import com.tphone.dto.response.ReviewResponse;
import com.tphone.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<ReviewResponse> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @PutMapping("/{reviewId}/status")
    public ReviewResponse updateReviewStatus(
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewStatusRequest request
    ) {
        return reviewService.updateReviewStatus(reviewId, request);
    }
}