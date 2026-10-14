package com.architecture.solution.repository.projection;

import com.architecture.solution.enums.JobStatus;
import com.architecture.solution.enums.JobType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterJobSummary {
    private String id;

    private String title;

    private String description;

    private String recruiterId;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private JobStatus status;

    private JobType jobType;

    private Instant createdAt;

    private Long applicationCount;
}
