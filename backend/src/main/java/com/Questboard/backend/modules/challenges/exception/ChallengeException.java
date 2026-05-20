package com.Questboard.backend.modules.challenges.exception;

public class ChallengeException extends RuntimeException {
    public ChallengeException(String message) { super(message); }
    public ChallengeException(String message, Throwable t) { super(message, t); }
}
