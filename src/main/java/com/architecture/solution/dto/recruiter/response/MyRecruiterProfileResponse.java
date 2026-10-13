package com.architecture.solution.dto.recruiter.response;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.RecruiterJobResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyRecruiterProfileResponse {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("recruiter_job_response")
    private PageResponse<List<RecruiterJobResponse>> recruiterJobResponse;

}
