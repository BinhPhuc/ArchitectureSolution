package com.architecture.solution.controller.docs;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.ErrorResponse;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File Controller")
public interface FileApi {

    @Operation(
            summary = "Upload CV",
            description = "Upload a PDF CV and set it as the authenticated candidate's CV"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "CV uploaded"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "File is empty, not a readable PDF or has no pages",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing, invalid or expired access token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "User is not a candidate",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Candidate not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<FileUploadResponse>> uploadCV(MultipartFile file);
}
