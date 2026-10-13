package com.architecture.solution.service.impl;

import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.GetJobResponse;
import com.architecture.solution.dto.recruiter.request.UpdateRecruiterProfileRequest;
import com.architecture.solution.dto.recruiter.response.RecruiterResponse;
import com.architecture.solution.dto.recruiter.response.UpdateRecruiterProfileResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.entity.Recruiter;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.repository.RecruiterRepository;
import com.architecture.solution.service.RecruiterService;
import com.architecture.solution.util.PageUtils;
import com.architecture.solution.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterServiceImpl implements RecruiterService {
    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;

    @Override
    public RecruiterResponse getRecruiterById(String recruiterId, int page, int size) {
        Recruiter recruiter = recruiterRepository.findByUserIdAndIsDeletedFalse(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));
        Page<Job> pageJobs = jobRepository.findByRecruiterIdAndStatusAndIsDeletedFalse(recruiterId,
                JobStatus.OPEN, PageUtils.getDefaultPageable(page, size));
        PageResponse<List<GetJobResponse>> pageResponse = PageUtils.mapPageJobToPageResponse(pageJobs);
        return RecruiterResponse.builder()
                .userId(recruiter.getUserId())
                .companyName((recruiter.getCompanyName()))
                .createdAt(recruiter.getCreatedAt())
                .jobs(pageResponse).build();
    }

    @Override
    @Transactional
    public UpdateRecruiterProfileResponse updateRecruiterProfile(UpdateRecruiterProfileRequest request) {
        String userId = SecurityUtils.getUserId();
        Recruiter recruiter = recruiterRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("Can not fine recruiter"));
        if(request.getCompanyName() != null) {
            recruiter.setCompanyName(request.getCompanyName());
        }
        recruiterRepository.save(recruiter);
        return UpdateRecruiterProfileResponse.builder()
                .userId(recruiter.getUserId())
                .companyName(recruiter.getCompanyName())
                .lastModifiedAt(recruiter.getLastModifiedAt())
                .build();
    }

}
