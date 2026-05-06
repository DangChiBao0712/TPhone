package com.tphone.service.impl;

import com.tphone.dto.request.BrandRequest;
import com.tphone.dto.response.BrandResponse;
import com.tphone.entity.Brand;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.BrandRepository;
import com.tphone.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public List<BrandResponse> getAll() {
        return brandRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<BrandResponse> getAllActive() {
        return brandRepository.findAllByIsActiveTrueAndDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BrandResponse getById(Long id) {
        Brand brand = brandRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        return toResponse(brand);
    }

    @Override
    public BrandResponse create(BrandRequest request) {
        if (brandRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("Slug brand đã tồn tại");
        }

        Brand brand = new Brand();
        mapRequest(brand, request);

        return toResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = brandRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        if (!brand.getSlug().equals(request.getSlug()) && brandRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("Slug brand đã tồn tại");
        }

        mapRequest(brand, request);

        return toResponse(brandRepository.save(brand));
    }

    @Override
    public void delete(Long id) {
        Brand brand = brandRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        brand.setDeletedAt(LocalDateTime.now());
        brandRepository.save(brand);
    }

    private void mapRequest(Brand brand, BrandRequest request) {
        brand.setName(request.getName());
        brand.setSlug(request.getSlug());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        brand.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private BrandResponse toResponse(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getSlug(),
                brand.getDescription(),
                brand.getLogoUrl(),
                brand.getIsActive(),
                brand.getSortOrder()
        );
    }
}