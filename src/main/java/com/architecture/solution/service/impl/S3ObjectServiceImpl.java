package com.architecture.solution.service.impl;

import com.architecture.solution.config.properties.S3Properties;
import com.architecture.solution.service.S3ObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(S3Properties.class)
public class S3ObjectServiceImpl implements S3ObjectService {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public String putObject(String bucketName, String objectKey, File file) {
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucketName).key(objectKey).build(),
                file.toPath()
        );
        String endpoint = s3Properties.endpoint();
        return new StringBuilder().append(endpoint).append("/").append(bucketName).append("/").append(objectKey).toString();
    }
}
