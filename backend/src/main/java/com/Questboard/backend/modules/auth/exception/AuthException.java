package com.Questboard.backend.modules.auth.exception;

/**
 * Domain exception thrown when authentication cannot proceed.
 */
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
