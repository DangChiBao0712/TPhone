package com.tphone.service;

import com.tphone.dto.request.CreateCommentRequest;
import com.tphone.dto.request.UpdateCommentRequest;
import com.tphone.dto.request.UpdateCommentStatusRequest;
import com.tphone.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long accountId, CreateCommentRequest request);
    CommentResponse updateMyComment(Long accountId, Long commentId, UpdateCommentRequest request);
    void deleteMyComment(Long accountId, Long commentId);

    List<CommentResponse> getVisibleCommentsByProduct(Long productId);
    List<CommentResponse> getMyComments(Long accountId);

    List<CommentResponse> getAllComments();
    CommentResponse updateCommentStatus(Long commentId, UpdateCommentStatusRequest request);
}