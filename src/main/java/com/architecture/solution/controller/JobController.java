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
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.DoubleStream.builder;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
@Tag(name = "Job Controller")
public class JobController {
    private final JobService jobService;
    @Operation(summary = "get job by id", description = "retrieve a job by it's id")
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobByID(@PathVariable String jobId) {
        JobResponse jobResponse = jobService.getJobById(jobId);
        return ResponseEntity.ok(ApiResponse.success(jobResponse));
    }

    @Operation(summary = "get jobs by title", description = "retrieve jobs by title")
    @GetMapping("/title")
    public ResponseEntity<ApiResponse<PageResponse<List<JobResponse>>>> getJobsByTitle(@RequestParam(name = "page") int page,
                                                                                      @RequestParam(name = "size") int size,
                                                                                      @RequestParam(name = "title") String title) {
        PageResponse<List<JobResponse>> pageResponse = jobService.findByTitle(title,page,size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @Operation(summary = "get jobs by recruiterID", description = "retrieve jobs by recruited id")
    @GetMapping("/recruiter-id")
    public ResponseEntity<ApiResponse<PageResponse<List<JobResponse>>>> getJobsByRecruiterId(@RequestParam(name = "page") int page,
                                                                                      @RequestParam(name = "size") int size,
                                                                                      @RequestParam(name = "recruiterId") String recruiterId) {
        PageResponse<List<JobResponse>> pageResponse = jobService.findByRecruiterId(recruiterId,page,size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @Operation(summary = "get jobs by jobs type", description = "retrieve jobs by job type")
    @GetMapping("/job-type")
    public ResponseEntity<ApiResponse<PageResponse<List<JobResponse>>>> getJobsByJobType(@RequestParam(name = "page") int page,
                                                                                           @RequestParam(name = "size") int size,
                                                                                           @RequestParam(name = "jobType") JobType jobType) {
        PageResponse<List<JobResponse>> pageResponse = jobService.findByJobType(jobType,page,size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @Operation(summary = "get jobs by status", description = "retrieve jobs by status")
    @GetMapping("/job-status")
    public ResponseEntity<ApiResponse<PageResponse<List<JobResponse>>>> getJobsByStatus(@RequestParam(name = "page") int page,
                                                                                           @RequestParam(name = "size") int size,
                                                                                           @RequestParam(name = "status") JobStatus status) {
        PageResponse<List<JobResponse>> pageResponse = jobService.findByStatus(status,page,size);
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }



}
