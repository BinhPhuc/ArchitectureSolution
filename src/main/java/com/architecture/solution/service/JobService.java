package com.architecture.solution.service;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.response.JobResponse;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;

import java.util.List;

public interface JobService {
    JobResponse getJobById(String jobId);

    PageResponse<List<JobResponse>> findByTitle(String title, int page, int size);

    PageResponse<List<JobResponse>> findByRecruiterId(String recruiterId, int page, int size);

    PageResponse<List<JobResponse>> findByJobType(JobType jobType, int page, int size);

    PageResponse<List<JobResponse>> findByStatus(JobStatus jobStatus, int page, int size);
}
