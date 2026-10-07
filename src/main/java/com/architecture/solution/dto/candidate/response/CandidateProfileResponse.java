package com.architecture.solution.dto.candidate.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateProfileResponse {
    @JsonProperty("bio")
    private String bio;

    @JsonProperty("phone")
    private String phone;
}
