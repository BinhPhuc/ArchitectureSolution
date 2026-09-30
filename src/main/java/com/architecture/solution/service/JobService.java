package com.architecture.solution.service;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.JobResponse;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;

import java.util.List;

public interface JobService {
    JobResponse getJobById(String jobId);

    PageResponse<List<JobResponse>> searchJobs(String title, JobType jobType, JobStatus status,
                                               int page, int size);

    PageResponse<List<JobResponse>> findByRecruiterId(String recruiterId, int page, int size);
}
