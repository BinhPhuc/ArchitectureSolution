package com.architecture.solution.dto.candidate.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCandidateProfileResponse {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @JsonProperty("displayed_name")
    private String displayedName;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("cv_url")
    private String cvUrl;

    @JsonProperty("last_modified_at")
    private Instant lastModifiedAt;
}
