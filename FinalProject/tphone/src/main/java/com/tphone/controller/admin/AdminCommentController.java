package com.tphone.controller.admin;

import com.tphone.dto.request.UpdateCommentStatusRequest;
import com.tphone.dto.response.CommentResponse;
import com.tphone.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentResponse> getAllComments() {
        return commentService.getAllComments();
    }

    @PutMapping("/{commentId}/status")
    public CommentResponse updateCommentStatus(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentStatusRequest request
    ) {
        return commentService.updateCommentStatus(commentId, request);
    }
}