package com.architecture.solution.dto.recruiter.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecruiterProfileResponse {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("last_modified_at")
    private Instant lastModifiedAt;

}
