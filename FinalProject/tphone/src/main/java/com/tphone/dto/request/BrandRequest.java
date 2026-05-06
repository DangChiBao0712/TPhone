package com.tphone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BrandRequest {

    @NotBlank(message = "Brand name is required")
    private String name;

    @NotBlank(message = "Brand slug is required")
    private String slug;

    private String description;
    private String logoUrl;

    @NotNull(message = "isActive is required")
    private Boolean isActive;

    private Integer sortOrder;
}