package com.tphone.service.impl;

import com.tphone.dto.request.ProductCreateRequest;
import com.tphone.dto.request.ProductUpdateRequest;
import com.tphone.dto.response.PageResponse;
import com.tphone.dto.response.ProductResponse;
import com.tphone.entity.Brand;
import com.tphone.entity.Category;
import com.tphone.entity.Product;
import com.tphone.enums.ProductStatus;
import com.tphone.exception.BadRequestException;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.BrandRepository;
import com.tphone.repository.CategoryRepository;
import com.tphone.repository.ProductRepository;
import com.tphone.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private static final String IMAGE_URL_PREFIX = "/uploads/images/";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getAllActive() {
        return productRepository.findAllByStatusAndDeletedAtIsNull(ProductStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toResponse(product);
    }

    @Override
    public ProductResponse getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toResponse(product);
    }

    @Override
    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findAllByCategoryIdAndDeletedAtIsNull(categoryId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getByBrand(Long brandId) {
        return productRepository.findAllByBrandIdAndDeletedAtIsNull(brandId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("Slug đã tồn tại");
        }

        if (productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("SKU đã tồn tại");
        }

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findByIdAndDeletedAtIsNull(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        }

        Product product = new Product();
        product.setCategory(category);
        product.setBrand(brand);

        mapCreate(product, request);

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSlug().equals(request.getSlug())
                && productRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("Slug đã tồn tại");
        }

        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("SKU đã tồn tại");
        }

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findByIdAndDeletedAtIsNull(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        }

        product.setCategory(category);
        product.setBrand(brand);

        mapUpdate(product, request);

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    public PageResponse<ProductResponse> searchProducts(
            String keyword,
            Long categoryId,
            Long brandId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));

        Page<Product> pageResult = productRepository.searchProducts(
                normalize(keyword),
                categoryId,
                brandId,
                minPrice,
                maxPrice,
                pageable
        );

        return new PageResponse<>(
                pageResult.getContent().stream().map(this::toResponse).toList(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1
                ? Sort.Direction.fromString(parts[1])
                : Sort.Direction.DESC;

        return Sort.by(direction, field);
    }

    private String normalize(String keyword) {
        return (keyword == null || keyword.isBlank()) ? null : keyword.trim();
    }

    private void mapCreate(Product p, ProductCreateRequest r) {
        p.setName(r.getName());
        p.setSlug(r.getSlug());
        p.setSku(r.getSku());
        p.setShortDescription(r.getShortDescription());
        p.setDescription(r.getDescription());
        p.setPrice(r.getPrice());
        p.setCompareAtPrice(r.getCompareAtPrice());
        p.setStockQuantity(r.getStockQuantity());
        p.setMinStockAlert(r.getMinStockAlert());
        p.setThumbnailUrl(validateImage(r.getThumbnailUrl()));
        p.setWeightGrams(r.getWeightGrams());
        p.setStatus(ProductStatus.valueOf(r.getStatus()));
    }

    private void mapUpdate(Product p, ProductUpdateRequest r) {
        p.setName(r.getName());
        p.setSlug(r.getSlug());
        p.setSku(r.getSku());
        p.setShortDescription(r.getShortDescription());
        p.setDescription(r.getDescription());
        p.setPrice(r.getPrice());
        p.setCompareAtPrice(r.getCompareAtPrice());
        p.setStockQuantity(r.getStockQuantity());
        p.setMinStockAlert(r.getMinStockAlert());
        p.setThumbnailUrl(validateImage(r.getThumbnailUrl()));
        p.setWeightGrams(r.getWeightGrams());
        p.setStatus(ProductStatus.valueOf(r.getStatus()));
    }

    private String validateImage(String url) {
        if (url == null || url.isBlank()) return null;

        if (!url.startsWith(IMAGE_URL_PREFIX)) {
            throw new BadRequestException("Ảnh phải nằm trong /uploads/images/");
        }

        return url;
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getBrand() != null ? p.getBrand().getId() : null,
                p.getBrand() != null ? p.getBrand().getName() : null,
                p.getName(),
                p.getSlug(),
                p.getSku(),
                p.getShortDescription(),
                p.getDescription(),
                p.getPrice(),
                p.getCompareAtPrice(),
                p.getStockQuantity(),
                p.getMinStockAlert(),
                p.getThumbnailUrl(),
                p.getWeightGrams(),
                p.getStatus() != null ? p.getStatus().name() : null
        );
    }
}