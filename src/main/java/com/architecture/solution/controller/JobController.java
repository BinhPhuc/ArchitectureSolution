package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.response.JobResponse;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import com.architecture.solution.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
@Tag(name = "Job Controller")
public class JobController {
    private final JobService jobService;

    @Operation(summary = "Get job by id", description = "Retrieve a job by it's id")
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobByID(@PathVariable String jobId) {
        JobResponse jobResponse = jobService.getJobById(jobId);
        return ResponseEntity.ok(ApiResponse.success(jobResponse));
    }

    @Operation(summary = "Search jobs", description = "Search jobs by title, job type and status. All filters are optional")
    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResponse<List<JobResponse>>>> searchJobs(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                                   @RequestParam(name = "size", defaultValue = "10") int size,
                                                                                   @RequestParam(name = "title", required = false) String title,
                                                                                   @RequestParam(name = "jobType", required = false) JobType jobType,
                                                                                   @RequestParam(name = "status", required = false) JobStatus status) {
        PageResponse<List<JobResponse>> pageResponse = jobService.searchJobs(title, jobType,
                status, page, size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    // TODO: move this method to RecruiterController later
    @Operation(summary = "Get jobs by recruiterID", description = "Retrieve jobs by recruited id")
    @GetMapping("/recruiter-id/{recruiterId}")
    public ResponseEntity<ApiResponse<PageResponse<List<JobResponse>>>> getJobsByRecruiterId(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                                                                             @PathVariable String recruiterId) {
        PageResponse<List<JobResponse>> pageResponse = jobService.findByRecruiterId(recruiterId,
                page, size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }
}
