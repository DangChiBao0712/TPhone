package com.tphone.controller.admin;

import com.tphone.dto.request.CategoryRequest;
import com.tphone.dto.response.ApiResponse;
import com.tphone.dto.response.CategoryResponse;
import com.tphone.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAll() {
        return new ApiResponse<>(true, "Get categories successfully", categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable Long id) {
        return new ApiResponse<>(true, "Get category successfully", categoryService.getById(id));
    }

    @PostMapping
    public ApiResponse<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return new ApiResponse<>(true, "Create category successfully", categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return new ApiResponse<>(true, "Update category successfully", categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return new ApiResponse<>(true, "Delete category successfully", null);
    }
}