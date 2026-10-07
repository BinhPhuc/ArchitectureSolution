package com.architecture.solution.dto.job.request;

import com.architecture.solution.enums.JobStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJobStatusRequest {
    @JsonProperty("status")
    private JobStatus jobStatus;
}
