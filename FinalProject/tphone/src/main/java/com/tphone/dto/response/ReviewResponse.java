package com.tphone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long accountId;
    private String accountName;
    private Long productId;
    private String productName;
    private Long orderId;
    private Integer rating;
    private String title;
    private String content;
    private String status;
}