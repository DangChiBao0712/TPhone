package com.tphone.controller.admin;

import com.tphone.dto.response.UploadFileResponse;
import com.tphone.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/uploads")
@RequiredArgsConstructor
public class AdminUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/images")
    public UploadFileResponse uploadImage(@RequestParam("file") MultipartFile file) {
        return fileStorageService.storeImage(file);
    }
}