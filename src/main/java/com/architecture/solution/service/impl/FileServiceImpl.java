package com.architecture.solution.service.impl;

import com.architecture.solution.constant.BucketConstant;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import com.architecture.solution.entity.Candidate;
import com.architecture.solution.entity.File;
import com.architecture.solution.enums.FileStatus;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.CandidateRepository;
import com.architecture.solution.repository.FileRepository;
import com.architecture.solution.service.FileService;
import com.architecture.solution.service.S3ObjectService;
import com.architecture.solution.util.SecurityUtils;
import com.architecture.solution.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private static final String CV_BUCKET = BucketConstant.CV_BUCKET;
    private final S3ObjectService s3ObjectService;
    private final FileRepository fileRepository;
    private final CandidateRepository candidateRepository;

    @Override
    @Transactional
    public FileUploadResponse uploadCV(MultipartFile file) {
        String userId = SecurityUtils.getUserId();
        Candidate candidate = candidateRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        FileUtils.validatePdf(file);
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long sizeBytes = file.getSize();
        String idempotencyKey = UUID.randomUUID().toString();
        String objectKey = new StringBuilder().append(userId).append("/").append(idempotencyKey).toString();
        s3ObjectService.putObject(CV_BUCKET, objectKey, file);
        File newFile = File.builder()
                .bucket(CV_BUCKET)
                .objectKey(objectKey)
                .originalFilename(originalFilename)
                .contentType(contentType)
                .sizeBytes(sizeBytes)
                .status(FileStatus.READY)
                .ownerId(userId)
                .build();
        fileRepository.save(newFile);
        String cvFileId = newFile.getId();
        candidate.setCvFileId(cvFileId);
        candidateRepository.save(candidate);
        return FileUploadResponse
                .builder()
                .id(cvFileId)
                .bucket(CV_BUCKET)
                .objectKey(objectKey)
                .originalFilename(originalFilename)
                .contentType(contentType)
                .sizeBytes(sizeBytes)
                .status(FileStatus.READY)
                .build();
    }
}
