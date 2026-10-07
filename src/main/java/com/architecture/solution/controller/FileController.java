package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.FileApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import com.architecture.solution.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController implements FileApi {
    private final FileService fileService;

    @Override
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/upload/cv")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadCV(
            @RequestParam("file") MultipartFile file
    ) {
        FileUploadResponse response = fileService.uploadCV(file, false);
        return ResponseEntity.ok(ApiResponse.success(response, "File uploaded successfully"));
    }
}
