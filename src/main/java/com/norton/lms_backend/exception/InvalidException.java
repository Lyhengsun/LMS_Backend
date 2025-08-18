package com.norton.lms_backend.exception;

public class InvalidException extends RuntimeException {
    public InvalidException(String message) {
        super(message);
    }
}
