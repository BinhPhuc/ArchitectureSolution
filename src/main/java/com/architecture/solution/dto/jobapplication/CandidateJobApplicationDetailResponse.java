package com.architecture.solution.dto.jobapplication;

import com.architecture.solution.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateJobApplicationDetailResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private ApplicationStatus status;

    @JsonProperty("cv_file_id")
    private String cvFileId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("salary_min")
    private BigDecimal salaryMin;

    @JsonProperty("salary_max")
    private BigDecimal salaryMax;
}
