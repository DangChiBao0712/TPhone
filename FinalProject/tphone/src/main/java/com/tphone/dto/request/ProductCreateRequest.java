package com.tphone.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotNull(message = "Category id is required")
    private Long categoryId;

    private Long brandId;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Product slug is required")
    private String slug;

    @NotBlank(message = "Product sku is required")
    private String sku;

    private String shortDescription;
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be >= 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Compare price must be >= 0")
    private BigDecimal compareAtPrice;

    private Integer stockQuantity;
    private Integer minStockAlert;
    private String thumbnailUrl;
    private Integer weightGrams;
    private String status;
}