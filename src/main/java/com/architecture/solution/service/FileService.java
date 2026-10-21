package com.architecture.solution.service;

import com.architecture.solution.dto.file.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileUploadResponse uploadItemImage(MultipartFile file);
}
