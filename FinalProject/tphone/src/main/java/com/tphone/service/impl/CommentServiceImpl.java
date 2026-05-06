package com.tphone.service.impl;

import com.tphone.dto.request.CreateCommentRequest;
import com.tphone.dto.request.UpdateCommentRequest;
import com.tphone.dto.request.UpdateCommentStatusRequest;
import com.tphone.dto.response.CommentResponse;
import com.tphone.entity.Account;
import com.tphone.entity.Comment;
import com.tphone.entity.Product;
import com.tphone.enums.CommentStatus;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.AccountRepository;
import com.tphone.repository.CommentRepository;
import com.tphone.repository.ProductRepository;
import com.tphone.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Override
    public CommentResponse createComment(Long accountId, CreateCommentRequest request) {
        if (request.getProductId() == null) {
            throw new BadRequestException("Product id is required");
        }

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BadRequestException("Comment content is required");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Product product = productRepository.findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findByIdAndDeletedAtIsNull(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            if (!parent.getProduct().getId().equals(product.getId())) {
                throw new BadRequestException("Parent comment does not belong to this product");
            }
        }

        Comment comment = new Comment();
        comment.setProduct(product);
        comment.setAccount(account);
        comment.setParent(parent);
        comment.setContent(request.getContent());
        comment.setStatus(CommentStatus.VISIBLE);

        return toResponse(commentRepository.save(comment));
    }

    @Override
    public CommentResponse updateMyComment(Long accountId, Long commentId, UpdateCommentRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BadRequestException("Comment content is required");
        }

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getAccount().getId().equals(accountId)) {
            throw new BadRequestException("Comment does not belong to current user");
        }

        comment.setContent(request.getContent());

        return toResponse(commentRepository.save(comment));
    }

    @Override
    public void deleteMyComment(Long accountId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getAccount().getId().equals(accountId)) {
            throw new BadRequestException("Comment does not belong to current user");
        }

        comment.setDeletedAt(LocalDateTime.now());
        comment.setStatus(CommentStatus.DELETED);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentResponse> getVisibleCommentsByProduct(Long productId) {
        return commentRepository.findAllByProductIdAndStatusAndDeletedAtIsNull(productId, CommentStatus.VISIBLE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CommentResponse> getMyComments(Long accountId) {
        return commentRepository.findAllByAccountIdAndDeletedAtIsNull(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CommentResponse> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .filter(comment -> comment.getDeletedAt() == null)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CommentResponse updateCommentStatus(Long commentId, UpdateCommentStatusRequest request) {
        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        CommentStatus status;
        try {
            status = CommentStatus.valueOf(request.getStatus().toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid comment status");
        }

        comment.setStatus(status);
        return toResponse(commentRepository.save(comment));
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getProduct() != null ? comment.getProduct().getId() : null,
                comment.getProduct() != null ? comment.getProduct().getName() : null,
                comment.getAccount() != null ? comment.getAccount().getId() : null,
                comment.getAccount() != null ? comment.getAccount().getFullName() : null,
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getContent(),
                comment.getStatus() != null ? comment.getStatus().name() : null
        );
    }
}