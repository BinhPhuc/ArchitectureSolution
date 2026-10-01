package com.architecture.solution.service;

import java.io.File;

public interface S3ObjectService {
    String putObject(String bucketName, String objectKey, File file);
}
