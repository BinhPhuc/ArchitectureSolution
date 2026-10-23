package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.FileApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import com.architecture.solution.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController implements FileApi {
    private final FileService fileService;

    @Override
    @PostMapping(value = "/item-images")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadItemImage(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(fileService.uploadItemImage(file)));
    }
}
