package com.architecture.solution.dto.recruiter.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterProfileResponse {
    @JsonProperty("company_name")
    private String companyName;
}
