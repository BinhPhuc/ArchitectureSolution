package com.architecture.solution.service;

import com.architecture.solution.dto.file.response.CvUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    CvUploadResponse uploadCV(MultipartFile file);
}
