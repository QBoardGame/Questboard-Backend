package com.Questboard.backend.modules.auth.exception;

import org.springframework.http.HttpStatus;

/**
 * Domain exception thrown when authentication cannot proceed.
 */
public class AuthException extends RuntimeException {

    private final HttpStatus status;

    public AuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AuthException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
