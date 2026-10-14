package com.architecture.solution.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileRequest {
    @Email
    private String email;

    @JsonProperty("displayed_name")
    private String displayedName;
}
