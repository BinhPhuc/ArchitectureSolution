package com.architecture.solution.repository.projection;

import com.architecture.solution.enums.ApplicationStatus;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateJobApplicationDetail {
    private String id;
    private String candidateId;
    private ApplicationStatus status;
    private String cvFileId;
    private String title;
    private String description;
    private String companyName;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
}
