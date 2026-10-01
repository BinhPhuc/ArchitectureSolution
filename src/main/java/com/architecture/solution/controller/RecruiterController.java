package com.architecture.solution.controller;

import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.JobResponse;
import com.architecture.solution.dto.recruiter.response.RecruiterResponse;
import com.architecture.solution.service.RecruiterService;
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
@RequestMapping("/api/v1/recruiters")
@Tag(name = "Recruiter Controller")
public class RecruiterController {
    private final RecruiterService recruiterService;

    @Operation(summary = "Get recruiter by id", description = "Get recruiter and open jobs they created by recruiter id")
    @GetMapping("/{recruiterId}")
    ResponseEntity<ApiResponse<RecruiterResponse>> searchById(@PathVariable String recruiterId,
                                                              @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                              @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        RecruiterResponse recruiterResponse = recruiterService.getRecruiterByRecruiterId(recruiterId, page, size);
        return ResponseEntity.ok(ApiResponse.success(recruiterResponse));
    }
}
