package com.architecture.solution.validator;

import com.architecture.solution.entity.Job;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobOwnershipValidator {
    private final JobRepository jobRepository;

    public Job getOwnedJob(String jobId) {
        Job job = jobRepository.findByIdAndIsDeletedFalse(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!job.getRecruiterId().equals(SecurityUtils.getUserId())) {
            throw new AccessDeniedException("You are not the owner of this job");
        }
        return job;
    }
}
