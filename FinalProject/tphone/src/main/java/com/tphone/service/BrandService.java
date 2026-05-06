package com.tphone.service;

import com.tphone.dto.request.BrandRequest;
import com.tphone.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAll();
    List<BrandResponse> getAllActive();
    BrandResponse getById(Long id);
    BrandResponse create(BrandRequest request);
    BrandResponse update(Long id, BrandRequest request);
    void delete(Long id);
}