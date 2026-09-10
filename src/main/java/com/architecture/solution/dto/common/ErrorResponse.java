package com.architecture.solution.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class ErrorResponse {
    @JsonProperty("status_code")
    private int statusCode;

    private String error;

    private String message;

    private String path;

    private Instant timestamp;
}
