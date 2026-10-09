package com.architecture.solution.dto.jobapplication;

import com.architecture.solution.enums.ApplicationStatus;
import com.architecture.solution.enums.JobType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetJobApplicationByCandidateIdResponse {
    @JsonProperty("candidate_id")
    private String candidateId;

    @JsonProperty("job_id")
    private String jobId;

    private String title;

    private String description;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("job_type")
    private JobType jobType;

    private ApplicationStatus status;
}
