
package com.architecture.solution.service;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.jobapplication.CandidateJobApplicationDetailResponse;
import com.architecture.solution.dto.jobapplication.ApplyForJobResponse;
import com.architecture.solution.dto.jobapplication.CandidateJobApplicationSummaryResponse;
import com.architecture.solution.dto.jobapplication.RecruiterJobApplicationSummaryResponse;
import com.architecture.solution.dto.jobapplication.RecruiterJobApplicationDetailResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobApplicationService {
    ApplyForJobResponse applyForJob(String jobId, MultipartFile cv);

    PageResponse<List<RecruiterJobApplicationSummaryResponse>> getRecruiterJobApplicationSummaries(String jobId, int page, int size);

    RecruiterJobApplicationDetailResponse getRecruiterJobApplicationDetail(String applicationId);

    PageResponse<List<CandidateJobApplicationSummaryResponse>> getCandidateJobApplicationSummaries(int page, int size);

    CandidateJobApplicationDetailResponse getCandidateJobApplicationDetail(String id);
}