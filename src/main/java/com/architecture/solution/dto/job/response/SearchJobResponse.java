package com.architecture.solution.dto.job.response;

import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchJobResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("recruiter_id")
    private String recruiterId;

    @JsonProperty("salary_min")
    private BigDecimal salaryMin;

    @JsonProperty("salary_max")
    private BigDecimal salaryMax;

    @JsonProperty("status")
    private JobStatus status;

    @JsonProperty("job_type")
    private JobType jobType;

    @JsonProperty("created_at")
    private Instant createdAt;
}
