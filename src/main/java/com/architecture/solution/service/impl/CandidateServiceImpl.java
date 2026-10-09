package com.architecture.solution.service.impl;

import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.candidate.response.GetApplicationResponse;
import com.architecture.solution.dto.candidate.response.UpdateCandidateProfileResponse;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.entity.Candidate;
import com.architecture.solution.repository.CandidateRepository;
import com.architecture.solution.repository.JobApplicationRepository;
import com.architecture.solution.repository.projection.CandidateJobApplication;
import com.architecture.solution.service.CandidateService;
import com.architecture.solution.util.PageUtils;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateServiceImpl implements CandidateService {
    private final CandidateRepository candidateRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    @Transactional
    public UpdateCandidateProfileResponse updateCandidateProfile(UpdateCandidateProfileRequest request) {
        String userId = SecurityUtils.getUserId();
        Candidate candidate = candidateRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
        if (request.getBio() != null) {
            candidate.setBio(request.getBio());
        }
        if (request.getPhone() != null) {
            candidate.setPhone(request.getPhone());
        }
        candidateRepository.save(candidate);
        return UpdateCandidateProfileResponse.builder()
                .userId(candidate.getUserId())
                .bio(candidate.getBio())
                .phone(candidate.getPhone())
                .lastModifiedAt(candidate.getLastModifiedAt())
                .build();
    }

    @Override
    public PageResponse<List<GetApplicationResponse>> getApplicationList(int page, int size) {
        String candidateId = SecurityUtils.getUserId();
        Page<CandidateJobApplication> projection = jobApplicationRepository.getJobApplicationById(candidateId, PageUtils.getDefaultPageable(page, size));
        return PageUtils.mapPageApplicationToPageResponse(projection);
    }
}
