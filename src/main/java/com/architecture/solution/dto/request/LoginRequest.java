package com.architecture.solution.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    // TODO: add more validation method
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
