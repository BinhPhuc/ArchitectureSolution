package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.RecruiterApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.RecruiterJobSummaryResponse;
import com.architecture.solution.dto.recruiter.request.UpdateRecruiterProfileRequest;
import com.architecture.solution.dto.recruiter.response.MyRecruiterProfileResponse;
import com.architecture.solution.dto.recruiter.response.RecruiterResponse;
import com.architecture.solution.dto.recruiter.response.UpdateRecruiterProfileResponse;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruiters")
public class RecruiterController implements RecruiterApi {
    private final RecruiterService recruiterService;

    @Override
    @GetMapping("/{recruiterId}")
    public ResponseEntity<ApiResponse<RecruiterResponse>> getById(
            @PathVariable String recruiterId,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        RecruiterResponse recruiterResponse = recruiterService.getRecruiterById(recruiterId, page, size);
        return ResponseEntity.ok(ApiResponse.success(recruiterResponse, "Recruiter retrieved successfully"));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UpdateRecruiterProfileResponse>> updateRecruiter(
            @RequestBody UpdateRecruiterProfileRequest request
    ) {
        UpdateRecruiterProfileResponse response = recruiterService.updateRecruiterProfile(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Recruiter update successfully"));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyRecruiterProfileResponse>> getMyProfile() {
        MyRecruiterProfileResponse response = recruiterService.getMyProfile();
        return ResponseEntity.ok(ApiResponse.success(response, "Recruiter profile retrieved successfully"));
    }

    @Override
    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/me/jobs")
    public ResponseEntity<ApiResponse<PageResponse<List<RecruiterJobSummaryResponse>>>> getMyJobs(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "status", required = false) JobStatus status
    ) {
        PageResponse<List<RecruiterJobSummaryResponse>> pageResponse = recruiterService.getMyJobs(title, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Jobs retrieved successfully"));
    }
}
