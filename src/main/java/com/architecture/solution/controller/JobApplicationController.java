package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.jobApplication.response.JobApplicationResponse;
import com.architecture.solution.repository.JobApplicationRepository;
import com.architecture.solution.service.JobApplicationService;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/applications")
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    @GetMapping("/{applicationId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> geJobApplicationById(@PathVariable String applicationId) {
        String candidateId = SecurityUtils.getUserId();
        JobApplicationResponse jobApplicationResponse = jobApplicationService.findApplicationById(applicationId,candidateId);
        return ResponseEntity.ok(ApiResponse.success(jobApplicationResponse));
    }
}
