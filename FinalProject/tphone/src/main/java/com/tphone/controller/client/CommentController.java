package com.tphone.controller.client;

import com.tphone.dto.request.CreateCommentRequest;
import com.tphone.dto.request.UpdateCommentRequest;
import com.tphone.dto.response.CommentResponse;
import com.tphone.dto.response.MessageResponse;
import com.tphone.security.model.CustomUserDetails;
import com.tphone.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/product/{productId}")
    public List<CommentResponse> getVisibleCommentsByProduct(@PathVariable Long productId) {
        return commentService.getVisibleCommentsByProduct(productId);
    }

    @GetMapping("/my")
    public List<CommentResponse> getMyComments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return commentService.getMyComments(userDetails.getAccountId());
    }

    @PostMapping
    public CommentResponse createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreateCommentRequest request
    ) {
        return commentService.createComment(userDetails.getAccountId(), request);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateMyComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request
    ) {
        return commentService.updateMyComment(userDetails.getAccountId(), commentId, request);
    }

    @DeleteMapping("/{commentId}")
    public MessageResponse deleteMyComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId
    ) {
        commentService.deleteMyComment(userDetails.getAccountId(), commentId);
        return new MessageResponse("Comment deleted successfully");
    }
}