package com.architecture.solution.dto.recruiter.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecruiterProfileRequest {
    @JsonProperty("company_name")
    private String companyName;
}
