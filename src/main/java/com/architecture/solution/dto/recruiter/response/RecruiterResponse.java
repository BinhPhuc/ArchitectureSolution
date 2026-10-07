package com.architecture.solution.dto.recruiter.response;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.GetJobResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterResponse {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("created_at")
    private Instant createdAt;
    @JsonProperty("jobs")
    private PageResponse<List<GetJobResponse>> jobs;
}
