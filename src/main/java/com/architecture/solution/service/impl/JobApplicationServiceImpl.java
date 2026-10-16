package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import com.architecture.solution.dto.jobapplication.ApplyForJobResponse;
import com.architecture.solution.dto.jobapplication.RecruiterJobApplicationSummaryResponse;
import com.architecture.solution.dto.jobapplication.UpdateApplicationStatusRequest;
import com.architecture.solution.dto.jobapplication.UpdateApplicationStatusResponse;
import com.architecture.solution.entity.JobApplication;
import com.architecture.solution.enums.ApplicationStatus;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.exception.ResourceExistsException;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobApplicationRepository;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.repository.projection.RecruiterJobApplicationSummary;
import com.architecture.solution.validator.JobOwnershipValidator;
import com.architecture.solution.service.FileService;
import com.architecture.solution.service.JobApplicationService;
import com.architecture.solution.util.PageUtils;
import com.architecture.solution.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationServiceImpl implements JobApplicationService {
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final FileService fileService;
    private final JobOwnershipValidator jobOwnershipValidator;

    @Override
    @Transactional
    public ApplyForJobResponse applyForJob(String jobId, MultipartFile cv) {
        String candidateId = SecurityUtils.getUserId();
        if (!jobRepository.existsByIdAndStatusAndIsDeletedFalse(jobId, JobStatus.OPEN)) {
            throw new ResourceNotFoundException("Open job not found");
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
        return ApplyForJobResponse.builder()
                .applicationId(jobApplication.getId())
                .jobId(jobId)
                .cvFileId(cvFileId)
                .status(ApplicationStatus.PENDING)
                .candidateId(candidateId)
                .build();
    }

    @Override
    public PageResponse<List<RecruiterJobApplicationSummaryResponse>> getRecruiterJobApplicationSummaries(String jobId,
                                                                                                          int page, int size) {
        jobOwnershipValidator.getOwnedJob(jobId);
        Page<RecruiterJobApplicationSummary> applications =
                jobApplicationRepository.findRecruiterJobApplicationSummariesByJobId(jobId,
                        PageUtils.getDefaultPageable(page, size));
        return PageUtils.mapPageRecruiterJobApplicationSummaryToPageResponse(applications);
    }

    @Override
    @Transactional
    public UpdateApplicationStatusResponse updateApplicationStatus(String applicationId,
                                                                   UpdateApplicationStatusRequest request) {
        JobApplication application = jobApplicationRepository.findByIdAndIsDeletedFalse(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));
        jobOwnershipValidator.getOwnedJob(application.getJobId());

        application.setStatus(ApplicationStatus.valueOf(request.getStatus()));
        JobApplication updatedApplication = jobApplicationRepository.saveAndFlush(application);

        return UpdateApplicationStatusResponse.builder()
                .applicationId(updatedApplication.getId())
                .candidateId(updatedApplication.getCandidateId())
                .jobId(updatedApplication.getJobId())
                .status(updatedApplication.getStatus())
                .cvFileId(updatedApplication.getCvFileId())
                .lastModifiedAt(updatedApplication.getLastModifiedAt())
                .build();
    }
}
