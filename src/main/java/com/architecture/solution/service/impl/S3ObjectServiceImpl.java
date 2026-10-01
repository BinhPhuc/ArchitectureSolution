package com.architecture.solution.service.impl;

import com.architecture.solution.exception.IllegalFileException;
import com.architecture.solution.service.S3ObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ObjectServiceImpl implements S3ObjectService {
    private final S3Client s3Client;

    @Override
    public void putObject(String bucketName, String objectKey, MultipartFile file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();
        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage());
            throw new IllegalFileException("Failed to upload file to S3: " + e.getMessage());
        }
    }
}
