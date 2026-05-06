package com.tphone.controller.admin;

import com.tphone.dto.request.ProductCreateRequest;
import com.tphone.dto.request.ProductUpdateRequest;
import com.tphone.dto.response.ApiResponse;
import com.tphone.dto.response.ProductResponse;
import com.tphone.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAll() {
        return new ApiResponse<>(true, "Get products successfully", productService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable Long id) {
        return new ApiResponse<>(true, "Get product successfully", productService.getById(id));
    }

    @PostMapping
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        return new ApiResponse<>(true, "Create product successfully", productService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        return new ApiResponse<>(true, "Update product successfully", productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable Long id) {
        productService.delete(id);
        return new ApiResponse<>(true, "Delete product successfully", null);
    }
}