package com.tphone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCommentRequest {

    @NotNull(message = "Product id is required")
    private Long productId;

    private Long parentId;

    @NotBlank(message = "Comment content is required")
    private String content;
}