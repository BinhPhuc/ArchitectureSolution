package com.architecture.solution.exception;

import org.springframework.http.HttpStatus;

public class WebClientException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;

    public WebClientException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
