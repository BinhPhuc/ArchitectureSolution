package com.architecture.solution.service;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.request.UpdateJobStatusRequest;
import com.architecture.solution.dto.job.response.ApplyJobResponse;
import com.architecture.solution.dto.job.response.GetJobResponse;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobService {
    GetJobResponse getJobById(String jobId);

    PageResponse<List<GetJobResponse>> searchJobs(String title, JobType jobType, JobStatus status,
                                                  int page, int size);

    PageResponse<List<GetJobResponse>> findByRecruiterId(String recruiterId, int page, int size);

    ApplyJobResponse applyForJob(String jobId, MultipartFile cv);

    GetJobResponse updateJobStatus(String jobId, UpdateJobStatusRequest request);
}
