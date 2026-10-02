package com.architecture.solution.dto.candidate.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCandidateProfileRequest {
    @Size(max = 2000)
    @JsonProperty("bio")
    private String bio;

    @Pattern(regexp = "^\\+?\\d{9,15}$")
    @JsonProperty("phone")
    private String phone;
}
