package com.architecture.solution.dto.job.response;

import com.architecture.solution.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplyJobResponse {
    @JsonProperty("application_id")
    private String applicationId;

    @JsonProperty("candidate_id")
    private String candidateId;

    @JsonProperty("job_id")
    private String jobId;

    private ApplicationStatus status;

    @JsonProperty("cv_file_id")
    private String cvFileId;
}
