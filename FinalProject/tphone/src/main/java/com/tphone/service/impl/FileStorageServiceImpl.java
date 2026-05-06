package com.tphone.service.impl;

import com.tphone.config.FileStorageProperties;
import com.tphone.dto.response.UploadFileResponse;
import com.tphone.exception.BadRequestException;
import com.tphone.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final FileStorageProperties fileStorageProperties;

    @Override
    public UploadFileResponse storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("Only jpg, png, webp, gif are allowed");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String extension = getExtension(originalFilename);

        String storedFileName = UUID.randomUUID() + (extension.isBlank() ? "" : "." + extension);

        try {
            Path uploadPath = Paths.get(fileStorageProperties.getUploadDir(), fileStorageProperties.getImageDir())
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/images/" + storedFileName;

            return new UploadFileResponse(
                    storedFileName,
                    fileUrl,
                    contentType,
                    file.getSize()
            );
        } catch (IOException ex) {
            throw new BadRequestException("Could not store image file");
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}