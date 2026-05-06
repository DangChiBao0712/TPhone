package com.tphone.service;

import com.tphone.dto.request.ProductCreateRequest;
import com.tphone.dto.request.ProductUpdateRequest;
import com.tphone.dto.response.ProductResponse;

import java.util.List;
import com.tphone.dto.response.PageResponse;
public interface ProductService {
    List<ProductResponse> getAll();
    List<ProductResponse> getAllActive();
    ProductResponse getById(Long id);
    ProductResponse getBySlug(String slug);
    List<ProductResponse> getByCategory(Long categoryId);
    List<ProductResponse> getByBrand(Long brandId);
    ProductResponse create(ProductCreateRequest request);
    ProductResponse update(Long id, ProductUpdateRequest request);
    void delete(Long id);
    PageResponse<ProductResponse> searchProducts(
            String keyword,
            Long categoryId,
            Long brandId,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            int page,
            int size,
            String sort
    );
}