package com.architecture.solution.infrastructure.s3.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(String accessKey, String secretKey, String region, String endpoint) {
}
