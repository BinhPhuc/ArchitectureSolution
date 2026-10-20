package com.architecture.solution.controller.docs;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File Controller")
public interface FileApi {
    @Operation(summary = "Upload a product image", description = "Upload a JPEG or PNG image as a seller")
    ResponseEntity<ApiResponse<FileUploadResponse>> uploadProductImage(MultipartFile file);
}
