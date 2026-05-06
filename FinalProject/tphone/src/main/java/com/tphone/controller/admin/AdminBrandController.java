package com.tphone.controller.admin;

import com.tphone.dto.request.BrandRequest;
import com.tphone.dto.response.ApiResponse;
import com.tphone.dto.response.BrandResponse;
import com.tphone.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandService brandService;

    @GetMapping
    public ApiResponse<List<BrandResponse>> getAll() {
        return new ApiResponse<>(true, "Get brands successfully", brandService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getById(@PathVariable Long id) {
        return new ApiResponse<>(true, "Get brand successfully", brandService.getById(id));
    }

    @PostMapping
    public ApiResponse<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
        return new ApiResponse<>(true, "Create brand successfully", brandService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BrandResponse> update(@PathVariable Long id, @Valid @RequestBody BrandRequest request) {
        return new ApiResponse<>(true, "Update brand successfully", brandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable Long id) {
        brandService.delete(id);
        return new ApiResponse<>(true, "Delete brand successfully", null);
    }
}