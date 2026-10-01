package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.JobResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterResponseimpl implements RecruiterService {

    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    @Override
    public RecruiterResponse getRecruiterByRecruiterId(String recruiterId, int page, int size) {
        Page<Job> pageJobs = jobRepository.findByRecruiterIdAndStatus(recruiterId, JobStatus.OPEN,PageUtils.getDefaultPageable(size, page));
        PageResponse<List<JobResponse>>  pageResponse = PageUtils.mapPageJobToPageResponse(pageJobs);
        Recruiter recruiter = recruiterRepository.findById(recruiterId).orElseThrow(() -> new ResourceNotFoundException("Can not found this recruiter id"));
        RecruiterResponse recruiterResponse = RecruiterResponse.builder()
                .userId(recruiter.getUserId())
                .companyName((recruiter.getCompanyName()))
                .createAt(recruiter.getCreatedAt())
                .pageResponse(pageResponse).build();
        return  recruiterResponse;
    }


}
