package com.architecture.solution.dto.user.response;

import com.architecture.solution.dto.ProfileResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String id;
    private String email;

    private String username;

    @JsonProperty("displayed_name")
    private String displayedName;

    private ProfileResponse profiles;
}
