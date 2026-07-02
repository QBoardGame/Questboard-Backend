package com.Questboard.backend.modules.auth.strategy;

import org.springframework.http.ResponseEntity;

import com.Questboard.backend.modules.auth.dto.request.ResetPasswordRequest;

public interface PasswordResetCapable {
    ResponseEntity<?> sendPasswordResetLink(String email);

    ResponseEntity<?> resetPassword(ResetPasswordRequest request);
}
