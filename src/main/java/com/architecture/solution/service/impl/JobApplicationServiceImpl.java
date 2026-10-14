package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import com.architecture.solution.dto.jobapplication.CandidateJobApplicationDetailResponse;
import com.architecture.solution.dto.jobapplication.ApplyForJobResponse;
import com.architecture.solution.dto.jobapplication.CandidateJobApplicationSummaryResponse;
import com.architecture.solution.dto.jobapplication.RecruiterJobApplicationSummaryResponse;
import com.architecture.solution.dto.jobapplication.RecruiterJobApplicationDetailResponse;
import com.architecture.solution.entity.JobApplication;
import org.springframework.security.access.AccessDeniedException;
import com.architecture.solution.enums.ApplicationStatus;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.exception.ResourceExistsException;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobApplicationRepository;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.repository.projection.CandidateJobApplicationSummary;
import com.architecture.solution.repository.projection.RecruiterJobApplicationSummary;
import com.architecture.solution.repository.projection.RecruiterJobApplicationDetail;
import com.architecture.solution.repository.projection.CandidateJobApplicationDetail;
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
    public RecruiterJobApplicationDetailResponse getRecruiterJobApplicationDetail(String applicationId) {
        JobApplication application = jobApplicationRepository.findByIdAndIsDeletedFalse(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));
        jobOwnershipValidator.getOwnedJob(application.getJobId());

        RecruiterJobApplicationDetail detail = jobApplicationRepository.findRecruiterJobApplicationDetailById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application details not found"));
        return RecruiterJobApplicationDetailResponse.builder()
                .id(detail.getId())
                .jobId(detail.getJobId())
                .candidateId(detail.getCandidateId())
                .cvFileId(detail.getCvFileId())
                .status(detail.getStatus())
                .createdAt(detail.getCreatedAt())
                .originalFileName(detail.getOriginalFileName())
                .candidateDisplayedName(detail.getCandidateDisplayedName())
                .email(detail.getEmail())
                .phone(detail.getPhone())
                .bio(detail.getBio())
                .build();
    }

    @Override
    public PageResponse<List<CandidateJobApplicationSummaryResponse>> getCandidateJobApplicationSummaries(int page, int size) {
        String candidateId = SecurityUtils.getUserId();
        Page<CandidateJobApplicationSummary> projection =
                jobApplicationRepository.findCandidateJobApplicationSummariesByCandidateId(candidateId,
                        PageUtils.getDefaultPageable(page, size));
        return PageUtils.mapPageCandidateJobApplicationSummaryToPageResponse(projection);
    }

    @Override
    public CandidateJobApplicationDetailResponse getCandidateJobApplicationDetail(String id) {
        String candidateId = SecurityUtils.getUserId();
        CandidateJobApplicationDetail detail = jobApplicationRepository.findCandidateJobApplicationDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not exist"));
        if (!candidateId.equals(detail.getCandidateId())) {
            throw new AccessDeniedException("You do not have permission to view this application");
        }
        return CandidateJobApplicationDetailResponse.builder()
                .id(detail.getId())
                .status(detail.getStatus())
                .cvFileId(detail.getCvFileId())
                .title(detail.getTitle())
                .description(detail.getDescription())
                .companyName(detail.getCompanyName())
                .salaryMin(detail.getSalaryMin())
                .salaryMax(detail.getSalaryMax())
                .build();
    }

}