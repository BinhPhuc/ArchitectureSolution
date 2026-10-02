package com.architecture.solution.service;

import com.architecture.solution.dto.recruiter.response.RecruiterResponse;

public interface RecruiterService {
    RecruiterResponse getRecruiterById(String recruiterId, int page, int size);
}
