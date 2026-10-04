package com.architecture.solution.dto;

import com.architecture.solution.dto.candidate.response.CandidateProfileResponse;
import com.architecture.solution.dto.recruiter.response.RecruiterProfileResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    @JsonProperty("candidate")
    private CandidateProfileResponse candidateProfile;
    @JsonProperty("recruiter")
    private RecruiterProfileResponse recruiterProfile;
}
