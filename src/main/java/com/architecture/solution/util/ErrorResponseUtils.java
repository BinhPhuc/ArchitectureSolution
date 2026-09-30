package com.architecture.solution.util;

import com.architecture.solution.dto.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public final class ErrorResponseUtils {
    private ErrorResponseUtils() {
    }

    public static ErrorResponse create(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .error(status.getReasonPhrase())
                .message(message)
                .statusCode(status.value())
                .timestamp(Instant.now())
                .path(path)
                .build();
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(create(status, message, path));
    }
}
