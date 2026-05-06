package com.tphone.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.file")
public class FileStorageProperties {
    private String uploadDir = "uploads";
    private String imageDir = "images";
}