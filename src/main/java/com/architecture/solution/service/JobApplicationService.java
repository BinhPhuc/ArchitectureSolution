package com.architecture.solution.service;

import com.architecture.solution.dto.jobApplication.response.JobApplicationResponse;

public interface JobApplicationService {
    JobApplicationResponse findApplicationById(String id, String candidateId);
}
