package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.JobApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.request.UpdateJobStatusRequest;
import com.architecture.solution.dto.job.response.ApplyJobResponse;
import com.architecture.solution.dto.job.response.GetJobResponse;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import com.architecture.solution.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobController implements JobApi {
    private final JobService jobService;

    @Override
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<GetJobResponse>> getJobByID(@PathVariable String jobId) {
        GetJobResponse jobResponse = jobService.getJobById(jobId);
        return ResponseEntity.ok(ApiResponse.success(jobResponse));
    }

    @Override
    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<List<GetJobResponse>>>> searchJobs(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "jobType", required = false) JobType jobType,
            @RequestParam(name = "status", required = false) JobStatus status
    ) {
        PageResponse<List<GetJobResponse>> pageResponse = jobService.searchJobs(title, jobType, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @Override
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/{jobId}/application")
    public ResponseEntity<ApiResponse<ApplyJobResponse>> applyForJob(
            @PathVariable String jobId,
            @RequestParam("cv") MultipartFile cv
    ) {
        ApplyJobResponse response = jobService.applyForJob(jobId, cv);
        return ResponseEntity.ok(ApiResponse.success(response, "Application submitted successfully"));
    }

    @Override
    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/{jobId}/status")
    public ResponseEntity<ApiResponse<GetJobResponse>> updateJobStatus(
            @PathVariable String jobId,
            @Valid @RequestBody UpdateJobStatusRequest request
    ) {
        GetJobResponse response = jobService.updateJobStatus(jobId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PreAuthorize("hasRole('RECRUITER')")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable String jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.ok(ApiResponse.success(null, "Job deleted successfully"));
    }
}
