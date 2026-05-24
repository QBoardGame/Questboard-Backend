package com.Questboard.backend.modules.auth.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.Questboard.backend.common.JwtUtil;
import com.Questboard.backend.modules.auth.dto.TokenPair;
import com.Questboard.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.model.RefreshToken;
import com.Questboard.backend.modules.auth.model.User;
import com.Questboard.backend.modules.auth.repository.RefreshTokenRepository;
import com.Questboard.backend.modules.auth.repository.UserRepository;

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
                user.getProvider().name(),
                user.getUsername());

        String newRefreshToken = jwtUtil.generateToken(
                user.getId(),
                129600,
                "refresh token",
                user.getRole().name(),
                user.getEmail(),
                user.getProvider().name(),
                user.getUsername());

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

    public TokenPair createTokens(User user) {

        String accessToken = jwtUtil.generateToken(user.getId(), 15, "access token", user.getRole().name(),
                user.getEmail(), user.getProvider().name(), user.getUsername());
        String refreshToken = jwtUtil.generateToken(user.getId(), 129600, "refresh token", user.getRole().name(),
                user.getEmail(), user.getProvider().name(), user.getUsername());

        saveRefreshToken(user, refreshToken);

        return new TokenPair(accessToken, refreshToken);
    }

    private void saveRefreshToken(User user, String token) {

        refreshTokenRepository.findByUser(user)
                .ifPresent(existingToken -> {
                    refreshTokenRepository.delete(existingToken);
                    refreshTokenRepository.flush();
                });

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusSeconds(60L * 60 * 24 * 90))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public void logout(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));

        refreshTokenRepository.findByUser(user)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }
}
