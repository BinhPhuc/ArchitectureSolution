package com.architecture.solution.repository.projection;

import com.architecture.solution.enums.ApplicationStatus;
import com.architecture.solution.enums.JobType;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationByCandidateId {
    private String candidateId;

    private String jobId;

    private String title;

    private String description;

    private String companyName;

    private JobType jobType;

    private ApplicationStatus status;
}
