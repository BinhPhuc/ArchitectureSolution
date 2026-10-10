package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.RecruiterApi;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.recruiter.response.RecruiterResponse;
import com.architecture.solution.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
