package com.architecture.solution.dto.candidate.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCandidateProfileRequest {
    @Email
    private String email;

    @Size(max = 100)
    @JsonProperty("displayed_name")
    private String displayedName;

    @Size(max = 2000)
    @JsonProperty("bio")
    private String bio;

    @Pattern(regexp = "^\\+?\\d{9,15}$")
    @JsonProperty("phone")
    private String phone;
}
