package com.tphone.service;

import com.tphone.dto.response.MessageResponse;
import com.tphone.dto.response.UploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {
    UploadFileResponse uploadProductImage(Long productId, MultipartFile file, String altText, Boolean isPrimary, Integer sortOrder);
    UploadFileResponse updateProductThumbnail(Long productId, MultipartFile file);
    MessageResponse deleteProductImage(Long imageId);
}