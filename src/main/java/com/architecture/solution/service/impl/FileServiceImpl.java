package com.architecture.solution.service.impl;

import com.architecture.solution.constant.BucketConstant;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import com.architecture.solution.entity.File;
import com.architecture.solution.enums.FileStatus;
import com.architecture.solution.infrastructure.s3.service.S3ObjectService;
import com.architecture.solution.repository.FileRepository;
import com.architecture.solution.repository.SellerRepository;
import com.architecture.solution.service.FileService;
import com.architecture.solution.util.FileUtils;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final S3ObjectService s3ObjectService;
    private final FileRepository fileRepository;
    private final SellerRepository sellerRepository;

    @Override
    @Transactional
    public FileUploadResponse uploadItemImage(MultipartFile file) {
        String userId = SecurityUtils.getUserId();
        sellerRepository.findById(userId)
                .filter(seller -> !Boolean.TRUE.equals(seller.getIsDeleted()))
                .orElseThrow(() -> new AccessDeniedException("An active seller profile is required"));
        FileUtils.validateImage(file);
        String bucket = BucketConstant.ITEM_IMAGE_BUCKET;
        String objectKey = userId + "/" + UUID.randomUUID();
        s3ObjectService.putObject(bucket, objectKey, file);
        File savedFile = fileRepository.save(File.builder()
                .bucket(bucket)
                .objectKey(objectKey)
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .sizeBytes(file.getSize())
                .status(FileStatus.READY)
                .ownerId(userId)
                .build());
        return FileUploadResponse.builder()
                .id(savedFile.getId())
                .bucket(savedFile.getBucket())
                .objectKey(savedFile.getObjectKey())
                .originalFilename(savedFile.getOriginalFilename())
                .contentType(savedFile.getContentType())
                .sizeBytes(savedFile.getSizeBytes())
                .status(savedFile.getStatus())
                .build();
    }
}
