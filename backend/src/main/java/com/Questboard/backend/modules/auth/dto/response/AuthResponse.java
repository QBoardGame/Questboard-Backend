package com.Questboard.backend.modules.auth.dto.response;

import lombok.Builder;

/**
 * Response payload returned after successful authentication.
 */
@Builder
public record AuthResponse(
        String email,
        String provider,
        String message,
        String accessToken,
        String refreshToken
) {
}
