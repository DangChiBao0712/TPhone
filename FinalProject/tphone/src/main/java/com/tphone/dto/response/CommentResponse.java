package com.tphone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long accountId;
    private String accountName;
    private Long parentId;
    private String content;
    private String status;
}