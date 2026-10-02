package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.SearchJobResponse;
import com.architecture.solution.dto.recruiter.response.RecruiterResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.entity.Recruiter;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.repository.RecruiterRepository;
import com.architecture.solution.service.RecruiterService;
import com.architecture.solution.util.PageUtils;
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
                .orElseThrow(() -> new ResourceNotFoundException("Can not found this recruiter id"));
        Page<Job> pageJobs = jobRepository.findByRecruiterIdAndStatusEqualsAndIsDeletedFalse(recruiterId,
                JobStatus.OPEN, PageUtils.getDefaultPageable(size, page));
        PageResponse<List<SearchJobResponse>> pageResponse = PageUtils.mapPageJobToPageResponse(pageJobs);
        return RecruiterResponse.builder()
                .userId(recruiter.getUserId())
                .companyName((recruiter.getCompanyName()))
                .createdAt(recruiter.getCreatedAt())
                .jobs(pageResponse).build();
    }
}
