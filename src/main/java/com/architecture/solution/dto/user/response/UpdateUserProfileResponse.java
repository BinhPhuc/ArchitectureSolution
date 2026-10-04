package com.architecture.solution.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileResponse {
    private String id;

    private String email;

    private String username;

    @JsonProperty("displayed_name")
    private String displayedName;

    @JsonProperty("last_modified_at")
    private Instant lastModifiedAt;
}
