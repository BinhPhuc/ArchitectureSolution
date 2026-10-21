package com.architecture.solution.infrastructure.s3.service.impl;

import com.architecture.solution.infrastructure.s3.service.S3BucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3BucketServiceImpl implements S3BucketService {
    private final S3Client s3Client;

    @Override
    public void createBucket(String bucketName) {
        if (!isBucketExists(bucketName)) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            log.info("S3 bucket created: {}", bucketName);
        } else {
            log.info("Bucket {} already created", bucketName);
        }
    }

    private boolean isBucketExists(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException exception) {
            return false;
        }
    }
}
