
package com.architecture.solution.service;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.jobapplication.ApplyForJobResponse;
import com.architecture.solution.dto.jobapplication.RecruiterJobApplicationSummaryResponse;
import com.architecture.solution.dto.jobapplication.UpdateApplicationStatusRequest;
import com.architecture.solution.dto.jobapplication.UpdateApplicationStatusResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobApplicationService {
    ApplyForJobResponse applyForJob(String jobId, MultipartFile cv);

    PageResponse<List<RecruiterJobApplicationSummaryResponse>> getRecruiterJobApplicationSummaries(String jobId, int page, int size);

    UpdateApplicationStatusResponse updateApplicationStatus(String applicationId,
                                                            UpdateApplicationStatusRequest request);
}
