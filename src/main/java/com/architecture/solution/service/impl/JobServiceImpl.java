package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.response.JobResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.service.JobService;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;

    public JobResponse getJobById(String jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(()-> new ResourceNotFoundException("Can not find this job"));
        JobResponse jobResponse = JobResponse.builder()
                .id(job.getId())
                .jobType(job.getJobType())
                .title(job.getTitle())
                .description(job.getDescription())
                .recruiterId(job.getRecruiterId())
                .salaryMax(job.getSalaryMax())
                .salaryMin(job.getSalaryMin())
                .status(job.getStatus())
                .build();
        return jobResponse;
    }
    public PageResponse<List<JobResponse>> mapPageJobToPageResponse(Page<Job> pageJob) {
        Page<JobResponse> pageJobResponse = pageJob.map(job -> {
            JobResponse jobResponse = new JobResponse();
            jobResponse.setJobType(job.getJobType());
            jobResponse.setId(job.getId());
            jobResponse.setDescription(job.getDescription());
            jobResponse.setTitle(job.getTitle());
            jobResponse.setStatus(job.getStatus());
            jobResponse.setSalaryMin(job.getSalaryMin());
            jobResponse.setSalaryMax(job.getSalaryMax());
            jobResponse.setRecruiterId(job.getRecruiterId());
            return jobResponse;
        });
        int totalPages = pageJobResponse.getTotalPages();
        int pageNum = pageJobResponse.getNumber();
        int pageSize = pageJobResponse.getSize();
        List<JobResponse> content = pageJobResponse.getContent();
        PageResponse<List<JobResponse>> pageResponse = PageResponse.
                <List<JobResponse>>builder()
                .totalPage(totalPages)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .content(content)
                .build();
        return pageResponse;
    }

    public PageResponse<List<JobResponse>> findByTitle(String title, int page, int size) {
        Pageable sortByCreateAt = PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<Job> pageJob =  jobRepository.findByTitleContaining(title, sortByCreateAt);
        return mapPageJobToPageResponse(pageJob);
    }
    public PageResponse<List<JobResponse>> findByRecruiterId(String recruiterId, int page, int size) {
        Pageable sortByCreateAt = PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<Job> pageJob = jobRepository.findByRecruiterId(recruiterId, sortByCreateAt);
        return mapPageJobToPageResponse(pageJob);
    }

    public  PageResponse<List<JobResponse>> findByJobType(JobType jobType, int page, int size) {
        Pageable sortByCreateAt = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Job> pageJob = jobRepository.findByJobType(jobType,sortByCreateAt);
        return mapPageJobToPageResponse(pageJob);
    }

    public PageResponse<List<JobResponse>> findByStatus(JobStatus status, int page, int size) {
        Pageable sortByCreateAt = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Job> pageJob = jobRepository.findByStatus(status,sortByCreateAt);
        return mapPageJobToPageResponse(pageJob);
    }
}
