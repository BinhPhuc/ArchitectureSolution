package com.architecture.solution.dto.job.request;

import com.architecture.solution.annotation.ValueOfEnum;
import com.architecture.solution.enums.JobType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobRequest {
    @NotBlank
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @DecimalMin("0")
    @JsonProperty("salary_min")
    private BigDecimal salaryMin;

    @DecimalMin("0")
    @JsonProperty("salary_max")
    private BigDecimal salaryMax;

    @NotNull
    @ValueOfEnum(enumClass = JobType.class)
    @JsonProperty("job_type")
    private String jobType;
}
