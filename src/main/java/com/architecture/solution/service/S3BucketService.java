package com.architecture.solution.service;

public interface S3BucketService {
    void createBucket(String bucketName);

    void deleteBucket(String bucketName);

    boolean isBucketExists(String bucketName);
}
