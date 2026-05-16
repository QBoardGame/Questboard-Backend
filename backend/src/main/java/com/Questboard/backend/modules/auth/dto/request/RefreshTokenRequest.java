package com.Questboard.backend.modules.auth.dto.request;

import lombok.Builder;

@Builder
public record RefreshTokenRequest(
        String refreshToken) {
}