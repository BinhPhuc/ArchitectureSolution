package com.architecture.solution.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorType {
    TOKEN_MISSING("Authentication token is missing"),
    TOKEN_EXPIRED("Authentication token has expired"),
    TOKEN_INVALID("Authentication token is invalid");

    public static final String REQUEST_ATTRIBUTE = "authErrorType";

    private final String message;
}
