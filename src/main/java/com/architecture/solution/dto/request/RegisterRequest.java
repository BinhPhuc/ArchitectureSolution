package com.architecture.solution.dto.request;

import com.architecture.solution.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("displayed_name")
    private String displayedName;

    @NotEmpty
    @JsonProperty("roles")
    private Set<RoleName> roles;
}
