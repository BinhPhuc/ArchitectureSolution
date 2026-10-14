package com.architecture.solution.exception;

public class BucketNotEmptyException extends RuntimeException {
    public BucketNotEmptyException(String message) {
        super(message);
    }

    public BucketNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
