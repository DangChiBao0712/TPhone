package com.tphone.repository;

import com.tphone.entity.Comment;
import com.tphone.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByProductIdAndDeletedAtIsNull(Long productId);

    List<Comment> findAllByProductIdAndParentIsNullAndDeletedAtIsNull(Long productId);

    List<Comment> findAllByParentIdAndDeletedAtIsNull(Long parentId);

    List<Comment> findAllByAccountIdAndDeletedAtIsNull(Long accountId);

    List<Comment> findAllByProductIdAndStatusAndDeletedAtIsNull(Long productId, CommentStatus status);

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);
}