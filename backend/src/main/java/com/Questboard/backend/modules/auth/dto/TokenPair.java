package com.Questboard.backend.modules.auth.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
