package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.response.JobResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.service.JobService;
import com.architecture.solution.util.PageResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;

    public JobResponse getJobById(String jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(
                "Can not find this job"));
        return JobResponse.builder()
                .id(job.getId())
                .jobType(job.getJobType())
                .title(job.getTitle())
                .description(job.getDescription())
                .recruiterId(job.getRecruiterId())
                .salaryMax(job.getSalaryMax())
                .salaryMin(job.getSalaryMin())
                .status(job.getStatus())
                .build();
    }

    public PageResponse<List<JobResponse>> searchJobs(String title, JobType jobType, JobStatus status,
                                                      int page, int size) {
        String titleFilter = StringUtils.hasText(title) ? title.trim() : null;
        Page<Job> pageJob = jobRepository.search(titleFilter, jobType, status,
                PageResponseUtil.getDefaultPageable(size, page));
        return PageResponseUtil.mapPageJobToPageResponse(pageJob);
    }

    public PageResponse<List<JobResponse>> findByRecruiterId(String recruiterId, int page,
                                                             int size) {
        Page<Job> pageJob = jobRepository.findByRecruiterId(recruiterId,
                PageResponseUtil.getDefaultPageable(size, page));
        return PageResponseUtil.mapPageJobToPageResponse(pageJob);
    }
}
