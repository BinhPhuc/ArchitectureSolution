package com.architecture.solution.controller.docs;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.ErrorResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.jobapplication.ApplyJobResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationByJobIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Job Application Controller")
public interface JobApplicationApi {

    @Operation(
            summary = "Apply for job",
            description = "Submit an application for a job with a PDF CV. Only candidates can apply"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Application submitted with status PENDING"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "CV is not a valid PDF or candidate has already applied for this job",
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
            description = "Job not found, deleted or not open, or candidate not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Concurrent duplicate application rejected by database constraint",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<ApplyJobResponse>> applyForJob(String jobId, MultipartFile cv);

    @Operation(
            summary = "Get job applications",
            description = "Retrieve applications of a job with candidate name and email. Only the recruiter who owns the job can view them"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Job applications retrieved"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid page or size",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing, invalid or expired access token",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "User does not have the RECRUITER role or does not own this job",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Job does not exist or has been deleted",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<PageResponse<List<GetJobApplicationByJobIdResponse>>>> getJobApplications(String jobId, int page, int size);
}
