package com.architecture.solution.dto.response;

import com.architecture.solution.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    @JsonProperty("expires_at")
    private Instant expiresAt;

    @JsonProperty("username")
    private String username;

    @JsonProperty("roles")
    private List<RoleName> roles;
}
