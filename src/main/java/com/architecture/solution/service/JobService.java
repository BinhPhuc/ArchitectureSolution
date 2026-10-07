package com.architecture.solution.service;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.ApplyJobResponse;
import com.architecture.solution.dto.job.response.SearchJobResponse;
import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobService {
    SearchJobResponse getJobById(String jobId);

    PageResponse<List<SearchJobResponse>> searchJobs(String title, JobType jobType, JobStatus status,
                                                     int page, int size);

    PageResponse<List<SearchJobResponse>> findByRecruiterId(String recruiterId, int page, int size);

    ApplyJobResponse applyForJob(String jobId, MultipartFile cv);
}
