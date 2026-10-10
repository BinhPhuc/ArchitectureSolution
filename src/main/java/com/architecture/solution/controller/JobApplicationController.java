package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.JobApplicationApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.jobapplication.ApplyJobResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationByJobIdResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationDetailResponse;
import com.architecture.solution.service.JobApplicationService;
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
@RequestMapping("/api/v1")
public class JobApplicationController implements JobApplicationApi {
    private final JobApplicationService jobApplicationService;

    @Override
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/jobs/{jobId}/application")
    public ResponseEntity<ApiResponse<ApplyJobResponse>> applyForJob(
            @PathVariable String jobId,
            @RequestParam("cv") MultipartFile cv
    ) {
        ApplyJobResponse response = jobApplicationService.applyForJob(jobId, cv);
        return ResponseEntity.ok(ApiResponse.success(response, "Application submitted successfully"));
    }

    @Override
    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<PageResponse<List<GetJobApplicationByJobIdResponse>>>> getJobApplications(
            @PathVariable String jobId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageResponse<List<GetJobApplicationByJobIdResponse>> response = jobApplicationService.getJobApplications(jobId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Job applications retrieved successfully"));
    }

    @Override
    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<GetJobApplicationDetailResponse>> getJobApplicationDetail(
            @PathVariable String applicationId
    ) {
        GetJobApplicationDetailResponse response = jobApplicationService.getJobApplicationDetail(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Job application retrieved successfully"));
    }

    @GetMapping("/{applicationId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> geJobApplicationById(
        @PathVariable String applicationId
    ) {
        String candidateId = SecurityUtils.getUserId();
        JobApplicationResponse jobApplicationResponse = jobApplicationService.findApplicationById(applicationId,candidateId);
        return ResponseEntity.ok(ApiResponse.success(jobApplicationResponse));
    }
}