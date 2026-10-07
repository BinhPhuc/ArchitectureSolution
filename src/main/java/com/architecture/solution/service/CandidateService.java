package com.architecture.solution.service;

import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.candidate.response.GetApplicationResponse;
import com.architecture.solution.dto.candidate.response.UpdateCandidateProfileResponse;
import com.architecture.solution.dto.common.PageResponse;

import java.util.List;

public interface CandidateService {
    UpdateCandidateProfileResponse updateCandidateProfile(UpdateCandidateProfileRequest request);

    PageResponse<List<GetApplicationResponse>> getApplicationList(int page, int size);
}
