package com.tphone.service.impl;

import com.tphone.dto.response.MessageResponse;
import com.tphone.dto.response.UploadFileResponse;
import com.tphone.entity.Product;
import com.tphone.entity.ProductImage;
import com.tphone.exception.ResourceNotFoundException;
import com.tphone.repository.ProductImageRepository;
import com.tphone.repository.ProductRepository;
import com.tphone.service.FileStorageService;
import com.tphone.service.FileStorageService;
import com.tphone.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public UploadFileResponse uploadProductImage(Long productId, MultipartFile file, String altText, Boolean isPrimary, Integer sortOrder) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        UploadFileResponse uploaded = fileStorageService.storeImage(file);

        if (Boolean.TRUE.equals(isPrimary)) {
            productImageRepository.findByProductIdAndIsPrimaryTrue(productId)
                    .ifPresent(existing -> {
                        existing.setIsPrimary(false);
                        productImageRepository.save(existing);
                    });
        }

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(uploaded.getFileUrl());
        productImage.setAltText(altText);
        productImage.setIsPrimary(Boolean.TRUE.equals(isPrimary));
        productImage.setSortOrder(sortOrder != null ? sortOrder : 0);

        productImageRepository.save(productImage);

        if (Boolean.TRUE.equals(isPrimary)) {
            product.setThumbnailUrl(uploaded.getFileUrl());
            productRepository.save(product);
        }

        return uploaded;
    }

    @Override
    @Transactional
    public UploadFileResponse updateProductThumbnail(Long productId, MultipartFile file) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        UploadFileResponse uploaded = fileStorageService.storeImage(file);

        productImageRepository.findByProductIdAndIsPrimaryTrue(productId)
                .ifPresent(existing -> {
                    existing.setIsPrimary(false);
                    productImageRepository.save(existing);
                });

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(uploaded.getFileUrl());
        productImage.setAltText(product.getName());
        productImage.setIsPrimary(true);
        productImage.setSortOrder(0);

        productImageRepository.save(productImage);

        product.setThumbnailUrl(uploaded.getFileUrl());
        productRepository.save(product);

        return uploaded;
    }

    @Override
    public MessageResponse deleteProductImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found"));

        productImageRepository.delete(productImage);

        return new MessageResponse("Product image deleted successfully");
    }
}