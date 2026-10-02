package com.architecture.solution.service.impl;

import com.architecture.solution.exception.BucketNotEmptyException;
import com.architecture.solution.service.S3BucketService;
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

    @Override
    public void deleteBucket(String bucketName) {
        if (isBucketExists(bucketName)) {
            try {
                s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
            } catch (S3Exception exception) {
                throw new BucketNotEmptyException(String.format("Bucket %s is not empty", bucketName));
            }
        } else {
            log.info("S3 bucket does not exist: {}", bucketName);
        }
    }

    @Override
    public boolean isBucketExists(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException exception) {
            return false;
        }
    }
}
