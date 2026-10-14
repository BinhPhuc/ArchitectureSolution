package com.architecture.solution.dto.recruiter.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyRecruiterProfileResponse {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("created_at")
    private Instant createdAt;
}
