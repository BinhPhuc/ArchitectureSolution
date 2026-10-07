package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import com.architecture.solution.dto.job.response.ApplyJobResponse;
import com.architecture.solution.dto.job.response.GetJobResponse;
import com.architecture.solution.entity.Job;
import com.architecture.solution.entity.JobApplication;
import com.architecture.solution.enums.ApplicationStatus;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import com.architecture.solution.exception.ResourceExistsException;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobApplicationRepository;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.service.FileService;
import com.architecture.solution.service.JobService;
import com.architecture.solution.util.PageUtils;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final FileService fileService;

    public GetJobResponse getJobById(String jobId) {
        Job job = jobRepository.findByIdAndIsDeletedFalse(jobId).orElseThrow(() -> new ResourceNotFoundException(
                "Can not find this job"));
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

    public PageResponse<List<GetJobResponse>> searchJobs(String title, JobType jobType, JobStatus status,
                                                         int page, int size) {
        String titleFilter = StringUtils.hasText(title) ? title.trim() : null;
        Page<Job> pageJob = jobRepository.search(titleFilter, jobType, status,
                PageUtils.getDefaultPageable(page, size));
        return PageUtils.mapPageJobToPageResponse(pageJob);
    }

    public PageResponse<List<GetJobResponse>> findByRecruiterId(String recruiterId, int page,
                                                                int size) {
        Page<Job> pageJob = jobRepository.findByRecruiterIdAndIsDeletedFalse(recruiterId,
                PageUtils.getDefaultPageable(page, size));
        return PageUtils.mapPageJobToPageResponse(pageJob);
    }

    @Override
    @Transactional
    public ApplyJobResponse applyForJob(String jobId, MultipartFile cv) {
        String candidateId = SecurityUtils.getUserId();
        if (!jobRepository.existsByIdAndStatusAndIsDeletedFalse(jobId, JobStatus.OPEN)) {
            throw new ResourceNotFoundException("Can not find this job");
        }
        if (jobApplicationRepository.existsByJobIdAndCandidateId(jobId, candidateId)) {
            throw new ResourceExistsException("You have already applied for this job");
        }
        FileUploadResponse cvResponse = fileService.uploadCV(cv, true);
        String cvFileId = cvResponse.getId();
        JobApplication jobApplication = JobApplication.builder()
                .jobId(jobId)
                .candidateId(candidateId)
                .cvFileId(cvFileId)
                .status(ApplicationStatus.PENDING)
                .build();
        jobApplicationRepository.save(jobApplication);
        return ApplyJobResponse.builder()
                .applicationId(jobApplication.getId())
                .jobId(jobId)
                .cvFileId(cvFileId)
                .status(ApplicationStatus.PENDING)
                .candidateId(candidateId)
                .build();
    }
}
