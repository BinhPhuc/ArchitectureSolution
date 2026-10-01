package com.architecture.solution.controller;

import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.candidate.response.UpdateCandidateProfileResponse;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/candidate")
@Tag(name = "Candidate Controller")
public class CandidateController {
    private final CandidateService candidateService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update candidate profile", description = "Updates the profile " +
            "information of the authenticated candidate.")
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UpdateCandidateProfileResponse>> updateCandidateProfile(@Valid @RequestBody UpdateCandidateProfileRequest request) {
        UpdateCandidateProfileResponse response = candidateService.updateCandidateProfile(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Candidate profile updated successfully"));
    }
}
