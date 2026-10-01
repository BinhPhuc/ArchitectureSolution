package com.architecture.solution.service.impl;

import com.architecture.solution.constant.BucketConstant;
import com.architecture.solution.dto.file.response.CvUploadResponse;
import com.architecture.solution.entity.Candidate;
import com.architecture.solution.exception.IllegalFileException;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.CandidateRepository;
import com.architecture.solution.service.FileService;
import com.architecture.solution.service.S3ObjectService;
import com.architecture.solution.util.FileUtils;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final S3ObjectService s3ObjectService;
    private final CandidateRepository candidateRepository;

    @Override
    public CvUploadResponse uploadCV(MultipartFile file) {
        try {
            File cv = FileUtils.convertFromMultipartToFile(file);
            String userId = SecurityUtils.getUserId();
            String key = new StringBuilder()
                    .append(userId)
                    .append("/")
                    .append(file.getOriginalFilename())
                    .toString();
            String url = s3ObjectService.putObject(BucketConstant.CV_BUCKET, key, cv);
            Candidate candidate = candidateRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User " +
                    "not " +
                    "found"));
            candidate.setCvUrl(url);
            candidateRepository.save(candidate);
            return CvUploadResponse.builder().cvUrl(url).build();
        } catch (IOException e) {
            throw new IllegalFileException("Failed to convert MultipartFile to File");
        }
    }
}
