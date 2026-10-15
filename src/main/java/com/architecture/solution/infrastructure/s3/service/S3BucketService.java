package com.architecture.solution.infrastructure.s3.service;

public interface S3BucketService {
    void createBucket(String bucketName);

    void deleteBucket(String bucketName);

    boolean isBucketExists(String bucketName);
}
