package com.architecture.solution.dto.jobapplication;

import com.architecture.solution.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetJobApplicationByJobIdResponse {
    private String id;

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("candidate_id")
    private String candidateId;

    @JsonProperty("cv_file_id")
    private String cvFileId;

    private ApplicationStatus status;

    @JsonProperty("original_file_name")
    private String originalFileName;

    @JsonProperty("candidate_displayed_name")
    private String candidateDisplayedName;

    private String email;
}
