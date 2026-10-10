
package com.architecture.solution.service;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.jobApplication.response.JobApplicationResponse;
import com.architecture.solution.dto.jobapplication.ApplyJobResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationByCandidateIdResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationByJobIdResponse;
import com.architecture.solution.dto.jobapplication.GetJobApplicationDetailResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobApplicationService {
    ApplyJobResponse applyForJob(String jobId, MultipartFile cv);

    PageResponse<List<GetJobApplicationByJobIdResponse>> getJobApplications(String jobId, int page, int size);

    GetJobApplicationDetailResponse getJobApplicationDetail(String applicationId);

    PageResponse<List<GetJobApplicationByCandidateIdResponse>> getApplicationList(int page, int size);

    JobApplicationResponse findApplicationById(String id, String candidateId);
}