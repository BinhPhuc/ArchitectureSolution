package com.architecture.solution.service.impl;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.file.response.FileUploadResponse;
import com.architecture.solution.dto.jobApplication.response.JobApplicationResponse;
import com.architecture.solution.dto.jobapplication.ApplyJobResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationByCandidateIdResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationByJobIdResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationDetailResponse;
import com.architecture.solution.entity.JobApplication;
import com.architecture.solution.enums.ApplicationStatus;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.exception.ResourceExistsException;
import com.architecture.solution.exception.ResourceNotFoundException;
import com.architecture.solution.repository.JobApplicationRepository;
import com.architecture.solution.repository.JobRepository;
import com.architecture.solution.repository.projection.JobApplicationByCandidateId;
import com.architecture.solution.repository.projection.JobApplicationByJobId;
import com.architecture.solution.repository.projection.JobApplicationDetail;
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
    public ApplyJobResponse applyForJob(String jobId, MultipartFile cv) {
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
        return ApplyJobResponse.builder()
                .applicationId(jobApplication.getId())
                .jobId(jobId)
                .cvFileId(cvFileId)
                .status(ApplicationStatus.PENDING)
                .candidateId(candidateId)
                .build();
    }

    @Override
    public PageResponse<List<GetJobApplicationByJobIdResponse>> getJobApplications(String jobId, int page, int size) {
        jobOwnershipValidator.getOwnedJob(jobId);
        Page<JobApplicationByJobId> applications = jobApplicationRepository.findJobApplicationByJobId(jobId,
                PageUtils.getDefaultPageable(page, size));
        return PageUtils.mapPageJobApplicationByJobIdToPageResponse(applications);
    }

    @Override
    public GetJobApplicationDetailResponse getJobApplicationDetail(String applicationId) {
        JobApplication application = jobApplicationRepository.findByIdAndIsDeletedFalse(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));
        jobOwnershipValidator.getOwnedJob(application.getJobId());

        JobApplicationDetail detail = jobApplicationRepository.findJobApplicationDetailById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application details not found"));
        return GetJobApplicationDetailResponse.builder()
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
    public PageResponse<List<GetJobApplicationByCandidateIdResponse>> getApplicationList(int page, int size) {
        String candidateId = SecurityUtils.getUserId();
        Page<JobApplicationByCandidateId> projection = jobApplicationRepository.getJobApplicationByCandidateId(candidateId,
                PageUtils.getDefaultPageable(page, size));
        return PageUtils.mapPageJobApplicationByCandidateIdToPageResponse(projection);
    }

    @Override
    public JobApplicationResponse findApplicationById(String id, String candidateId) {
        JobApplication jobApplication = jobApplicationRepository.findByIdAndCandidateId(id, candidateId).orElseThrow(()
                -> new ResourceNotFoundException("Application not exist"));
        return JobApplicationResponse.builder()
                .jobId(jobApplication.getJobId())
                .candidateId(jobApplication.getCandidateId())
                .cvFileId(jobApplication.getCvFileId())
                .status(jobApplication.getStatus())
                .id(jobApplication.getId())
                .createAt(jobApplication.getCreatedAt())
                .lastModifiedAt(jobApplication.getLastModifiedAt())
                .lastModifiedBy(jobApplication.getLastModifiedBy())
                .build();
    }
}