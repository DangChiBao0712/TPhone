package com.tphone.service;

import com.tphone.dto.response.UploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    UploadFileResponse storeImage(MultipartFile file);
}