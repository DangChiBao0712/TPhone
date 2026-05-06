package com.tphone.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateReviewRequest {
    private Integer rating;
    private String title;
    private String content;
}