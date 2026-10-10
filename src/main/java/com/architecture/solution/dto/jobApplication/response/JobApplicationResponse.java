package com.architecture.solution.dto.jobApplication.response;

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
public class JobApplicationResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private ApplicationStatus status;

    @JsonProperty("cv_file_id")
    private String cvFileId;

    @JsonProperty("create_at")
    private Instant createAt;

    @JsonProperty("last_modified_at")
    private Instant lastModifiedAt;

    @JsonProperty("last_modified_by")
    private String lastModifiedBy;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("salary_min")
    private BigDecimal salaryMin;

    @JsonProperty("salary_max")
    private BigDecimal salaryMax;
}
