package com.architecture.solution.controller;

import com.architecture.solution.controller.docs.CandidateApi;
import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.candidate.response.GetApplicationResponse;
import com.architecture.solution.dto.candidate.response.UpdateCandidateProfileResponse;
import com.architecture.solution.dto.common.ApiResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.service.CandidateService;
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
public class CandidateController implements CandidateApi {
    private final CandidateService candidateService;

    @Override
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UpdateCandidateProfileResponse>> updateCandidateProfile(
            @Valid @RequestBody UpdateCandidateProfileRequest request
    ) {
        UpdateCandidateProfileResponse response = candidateService.updateCandidateProfile(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Candidate profile updated successfully"));
    }

    @Override
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/me/applications")
    public ResponseEntity<ApiResponse<PageResponse<List<GetApplicationResponse>>>> getApplicationList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageResponse<List<GetApplicationResponse>> response = candidateService.getApplicationList(page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Candidate application list retrieved successfully"));
    }
}
