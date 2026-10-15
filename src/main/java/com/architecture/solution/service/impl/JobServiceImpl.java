package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.request.CreateJobRequest;
import com.architecture.solution.dto.job.request.UpdateJobStatusRequest;
import com.architecture.solution.dto.job.response.GetJobResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import com.architecture.solution.exception.InvalidArgumentException;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.validator.JobOwnershipValidator;
import com.architecture.solution.service.JobService;
import com.architecture.solution.util.PageUtils;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final JobOwnershipValidator jobOwnershipValidator;

    public GetJobResponse getJobById(String jobId) {
        Job job = jobRepository.findByIdAndIsDeletedFalse(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not " +
                "found"));
        return GetJobResponse.builder()
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

    public PageResponse<List<GetJobResponse>> searchJobs(String title, JobType jobType, JobStatus status, int page, int size) {
        String titleFilter = StringUtils.hasText(title) ? title.trim() : null;
        Page<Job> pageJob = jobRepository.search(titleFilter, jobType, status,
                PageUtils.getDefaultPageable(page, size));
        return PageUtils.mapPageJobToPageResponse(pageJob);
    }

    @Override
    @Transactional
    public GetJobResponse createJob(CreateJobRequest request) {
        if (request.getSalaryMin() != null && request.getSalaryMax() != null
                && request.getSalaryMin().compareTo(request.getSalaryMax()) > 0) {
            throw new InvalidArgumentException("Minimum salary must not be greater than maximum salary");
        }
        Job job = jobRepository.save(Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .recruiterId(SecurityUtils.getUserId())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .jobType(JobType.valueOf(request.getJobType()))
                .build());
        return GetJobResponse.builder()
                .id(job.getId())
                .jobType(job.getJobType())
                .title(job.getTitle())
                .description(job.getDescription())
                .recruiterId(job.getRecruiterId())
                .salaryMax(job.getSalaryMax())
                .salaryMin(job.getSalaryMin())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public GetJobResponse updateJobStatus(String jobId, UpdateJobStatusRequest request) {
        Job job = jobOwnershipValidator.getOwnedJob(jobId);
        job.setStatus(JobStatus.valueOf(request.getStatus()));
        jobRepository.save(job);
        return GetJobResponse.builder()
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
}
