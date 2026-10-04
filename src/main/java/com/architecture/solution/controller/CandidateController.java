package com.architecture.solution.controller;

import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.candidate.response.GetApplicationResponse;
import com.architecture.solution.dto.candidate.response.UpdateCandidateProfileResponse;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/candidates")
@Tag(name = "Candidate Controller")
public class CandidateController {
    private final CandidateService candidateService;

    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Update candidate profile", description = "Updates the profile " +
            "information of the authenticated candidate.")
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UpdateCandidateProfileResponse>> updateCandidateProfile(@Valid @RequestBody UpdateCandidateProfileRequest request) {
        UpdateCandidateProfileResponse response = candidateService.updateCandidateProfile(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Candidate profile updated successfully"));
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get candidate application list", description = "Retrieves the list of job applications submitted by " +
            "the authenticated candidate.")
    @GetMapping("/me/applications")
    public ResponseEntity<ApiResponse<PageResponse<List<GetApplicationResponse>>>> getApplicationList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageResponse<List<GetApplicationResponse>> response = candidateService.getApplicationList(page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Candidate application list retrieved successfully"));
    }
}
