package com.architecture.solution.dto.job.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterJobResponse {
    @JsonProperty("job_response")
    private GetJobResponse jobResponse;

    @JsonProperty("number_of_pending_job")
    private Long pendingCount;
}
