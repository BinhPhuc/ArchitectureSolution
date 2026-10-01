package com.architecture.solution.service;

import com.architecture.solution.dto.recruiter.response.RecruiterResponse;
import org.springframework.data.domain.Pageable;

public interface RecruiterService {
    RecruiterResponse getRecruiterByRecruiterId(String recruiterId, int page, int size);
}
