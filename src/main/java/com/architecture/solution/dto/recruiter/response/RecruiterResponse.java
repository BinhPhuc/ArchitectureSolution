package com.architecture.solution.dto.recruiter.response;

import com.architecture.solution.dto.common.PageResponse;
import com.architecture.solution.dto.job.response.JobResponse;
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
    @JsonProperty("creat_at")
    private Instant createAt;
    @JsonProperty("page_Response")
    private PageResponse<List<JobResponse>> pageResponse;

}
