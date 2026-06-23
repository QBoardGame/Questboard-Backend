package com.Questboard.backend.modules.auth.dto;

public record GoogleUser(
        String email,
        String providerId) {
}
