package com.architecture.solution.service;

import com.architecture.solution.dto.candidate.request.UpdateCandidateProfileRequest;
import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.RecruiterJobSummaryResponse;
import com.architecture.solution.dto.recruiter.request.UpdateRecruiterProfileRequest;
import com.architecture.solution.dto.recruiter.response.MyRecruiterProfileResponse;
import com.architecture.solution.dto.recruiter.response.RecruiterResponse;
import com.architecture.solution.dto.recruiter.response.UpdateRecruiterProfileResponse;
import com.architecture.solution.enums.JobStatus;

import java.util.List;

public interface RecruiterService {
    RecruiterResponse getRecruiterById(String recruiterId, int page, int size);

    UpdateRecruiterProfileResponse updateRecruiterProfile(UpdateRecruiterProfileRequest request);

    MyRecruiterProfileResponse getMyProfile();

    PageResponse<List<RecruiterJobSummaryResponse>> getMyJobs(String title, JobStatus status, int page, int size);
}
