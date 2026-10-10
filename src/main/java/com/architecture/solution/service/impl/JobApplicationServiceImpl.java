package com.architecture.solution.service.impl;

import com.architecture.solution.dto.jobApplication.response.JobApplicationResponse;
import com.architecture.solution.entity.JobApplication;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobApplicationRepository;
import com.architecture.solution.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationServiceImpl implements JobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public JobApplicationResponse findApplicationById(String id, String candidateId) {
        JobApplication jobApplication = jobApplicationRepository.findByIdAndCandidateId(id,candidateId).orElseThrow(()
                -> new ResourceNotFoundException("Application not exist"));
        return JobApplicationResponse.builder()
                .jobId(jobApplication.getJobId())
                .candidateId(jobApplication.getCandidateId())
                .cvFileId(jobApplication.getCvFileId())
                .status(jobApplication.getStatus())
                .id(jobApplication.getId())
                .createAt(jobApplication.getCreatedAt())
                .lastModifiedAt(jobApplication.getLastModifiedAt())
                .lastModifiedBy(jobApplication.getLastModifiedBy())
                .build();
    }
}
