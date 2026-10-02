package com.architecture.solution.config.initializer;

import com.architecture.solution.constant.BucketConstant;
import com.architecture.solution.service.S3BucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BucketInitializer implements ApplicationRunner {
    private final S3BucketService s3BucketService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing S3 bucket: {}", BucketConstant.CV_BUCKET);
        s3BucketService.createBucket(BucketConstant.CV_BUCKET);
    }
}
