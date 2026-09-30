package com.architecture.solution.service;

import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.candidate.response.UpdateCandidateProfileResponse;

public interface CandidateService {
    UpdateCandidateProfileResponse updateCandidateProfile(UpdateCandidateProfileRequest request);
}
