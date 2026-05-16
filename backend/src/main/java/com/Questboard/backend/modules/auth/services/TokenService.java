package com.Questboard.backend.modules.auth.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.Questboard.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.model.RefreshToken;
import com.Questboard.backend.modules.auth.model.User;
import com.Questboard.backend.modules.auth.repository.RefreshTokenRepository;
import com.Questboard.backend.modules.auth.repository.UserRepository;
import com.Questboard.backend.modules.auth.security.JwtUtil;

@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public TokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        if (request == null || !StringUtils.hasText(request.refreshToken())) {
            throw new AuthException("Refresh token is required");
        }

        String token = request.refreshToken();

        // 1. Validate JWT signature + expiry
        if (!jwtUtil.isTokenValid(token)) {
            throw new AuthException("Invalid or expired refresh token");
        }

        // 2. Extract userId
        String userId = jwtUtil.extractUserId(token);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new AuthException("User not found"));

        // 3. Validate token in DB
        RefreshToken storedToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new AuthException("Refresh token not found"));

        if (!storedToken.getToken().equals(token)) {
            throw new AuthException("Refresh token mismatch");
        }

        if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(Instant.now())) {
            throw new AuthException("Refresh token expired or revoked");
        }

        // 4. Generate new tokens
        String newAccessToken = jwtUtil.generateToken(
                user.getId(),
                15,
                "access token",
                user.getRole().name(),
                user.getEmail(), 
                user.getProvider().name());

        String newRefreshToken = jwtUtil.generateToken(
                user.getId(),
                129600,
                "refresh token",
                user.getRole().name(), 
                user.getEmail(), 
                user.getProvider().name());

        // 5. Rotate refresh token in DB
        storedToken.setToken(newRefreshToken);
        storedToken.setExpiryDate(Instant.now().plusSeconds(60L * 60 * 24 * 90));
        refreshTokenRepository.save(storedToken);

        // 6. Return response
        return AuthResponse.builder()
                .email(user.getEmail())
                .provider(user.getProvider().name())
                .message("Token refreshed successfully")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
