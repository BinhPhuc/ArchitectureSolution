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
    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String password;

    @NotNull
    @NotBlank
    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("displayed_name")
    private String displayedName;

    @NotNull
    @NotEmpty
    @JsonProperty("roles")
    private Set<@NotNull RoleName> roles;
}
