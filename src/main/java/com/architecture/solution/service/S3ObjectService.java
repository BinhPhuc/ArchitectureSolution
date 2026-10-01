package com.architecture.solution.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3ObjectService {
    void putObject(String bucketName, String objectKey, MultipartFile file);
}