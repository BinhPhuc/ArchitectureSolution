package com.architecture.solution.service.impl;

import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.candidate.response.UpdateCandidateProfileResponse;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.entity.Candidate;
import com.architecture.solution.repository.CandidateRepository;
import com.architecture.solution.service.CandidateService;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateServiceImpl implements CandidateService {
    private final CandidateRepository candidateRepository;

    @Override
    public UpdateCandidateProfileResponse updateCandidateProfile(UpdateCandidateProfileRequest request) {
        String userId = SecurityUtils.getUserId();
        Candidate candidate = candidateRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not " +
                "found"));
        // TODO: replace manual null checks with JSON Merge Patch
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
                .cvUrl(candidate.getCvUrl())
                .lastModifiedAt(candidate.getLastModifiedAt())
                .build();
    }
}
