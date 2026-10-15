package com.architecture.solution.controller.docs;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.ErrorResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.RecruiterJobSummaryResponse;
import com.architecture.solution.dto.recruiter.response.RecruiterResponse;
import com.architecture.solution.enums.JobStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Recruiter Controller")
public interface RecruiterApi {

    @Operation(
            summary = "Get recruiter by id",
            description = "Get recruiter and open jobs they created by recruiter id"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Recruiter found"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid page or size",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Recruiter not found or deleted",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<RecruiterResponse>> getById(String recruiterId, int page, int size);

    @Operation(
            summary = "Get my jobs",
            description = "List the non-deleted jobs owned by the authenticated recruiter, newest first, " +
                    "each with its number of non-deleted applications. Filter by title (partial match) and status, " +
                    "both optional. Only recruiters can use this endpoint"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Paged jobs of the recruiter. Each item includes application_count"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid page, size or status",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing, invalid or expired access token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "User is not a recruiter",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<PageResponse<List<RecruiterJobSummaryResponse>>>> getMyJobs(
            int page, int size, String title, JobStatus status);
}
