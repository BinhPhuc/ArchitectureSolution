package com.architecture.solution.controller.docs;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.ErrorResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.request.UpdateJobStatusRequest;
import com.architecture.solution.dto.job.response.ApplyJobResponse;
import com.architecture.solution.dto.job.response.GetJobResponse;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Job Controller")
public interface JobApi {

    @Operation(
            summary = "Get job by id",
            description = "Retrieve a job by its id"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Job found"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Job not found or deleted",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<GetJobResponse>> getJobByID(String jobId);

    @Operation(
            summary = "Search jobs",
            description = "Search jobs by title, job type and status. All filters are optional"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Jobs matching the filters"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid page, size, jobType or status",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<PageResponse<List<GetJobResponse>>>> searchJobs(
            int page, int size, String title, JobType jobType, JobStatus status);

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
            summary = "Update job status",
            description = "Change the status of a job. Only recruiters can update job status"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Job status updated"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid status",
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
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Job not found or deleted",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<GetJobResponse>> updateJobStatus(String jobId, UpdateJobStatusRequest request);

    @Operation(
            summary = "Delete job",
            description = "Soft delete a job and its category associations. " +
                    "Requires an authenticated user with the RECRUITER role who owns the job. " +
                    "Send the access token in the Authorization header as `Bearer <access-token>`. " +
                    "The job must exist and must not already be deleted. " +
                    "Jobs with non-deleted applications cannot be deleted; change their status to CLOSED instead. " +
                    "No request body is required. Repeating a successful deletion returns 404.",
            parameters = @Parameter(
                    name = "jobId",
                    in = ParameterIn.PATH,
                    required = true,
                    description = "ID of the job to delete",
                    schema = @Schema(type = "string"),
                    example = "6d29c3c0-9f74-4ac3-bb85-23f9c459af57"
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Job and its category associations soft deleted successfully. Response data is null",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                            name = "Job deleted",
                            value = """
                                    {
                                      "status_code": 200,
                                      "message": "Job deleted successfully",
                                      "data": null
                                    }
                                    """
                    )
            )
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
            description = "Job does not exist or has already been deleted",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Job has non-deleted applications. Change its status to CLOSED instead of deleting it",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    ResponseEntity<ApiResponse<Void>> deleteJob(String jobId);
}
