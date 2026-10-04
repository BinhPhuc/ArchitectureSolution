package com.architecture.solution.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotBlank
    @JsonProperty("current_password")
    private String currentPassword;

    @NotBlank
    @JsonProperty("new_password")
    private String newPassword;

    @NotBlank
    @JsonProperty("retype_new_password")
    private String retypeNewPassword;
}
