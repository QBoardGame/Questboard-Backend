package com.Questboard.backend.modules.auth.services.strategy;

import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.events.UserRegistrationEvent;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.model.AuthProvider;
import com.Questboard.backend.modules.auth.model.RefreshToken;
import com.Questboard.backend.modules.auth.model.User;
import com.Questboard.backend.modules.auth.repository.RefreshTokenRepository;
import com.Questboard.backend.modules.auth.repository.UserRepository;
import com.Questboard.backend.modules.auth.security.JwtUtil;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Authentication flow for email / password credentials.
 */
@Component("EMAIL")
public class EmailPasswordAuthStrategy implements AuthStrategy {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EmailPasswordAuthStrategy(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            RefreshTokenRepository refreshTokenRepository,
            ApplicationEventPublisher eventPublisher
        ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        validateRequest(request);

        User user = userRepository.findByEmailAndProvider(request.email(), AuthProvider.EMAIL)
                .orElseThrow(() -> new AuthException("Invalid credentials for email authentication"));

        if (!passwordEncoder.matches(request.password(), Optional.ofNullable(user.getPassword()).orElse(""))) {
            throw new AuthException("Invalid credentials for email authentication");
        }

        String accessToken = jwtUtil.generateToken(user.getId(), 15, "access token", user.getRole().name(),
                user.getEmail(), user.getProvider().name());
        String refreshToken = jwtUtil.generateToken(user.getId(), 129600, "refresh token", user.getRole().name(),
                user.getEmail(), user.getProvider().name());

        saveRefreshToken(user, refreshToken);
        return createResponse(user.getEmail(), user.getProvider().name(), accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request == null) {
            throw new AuthException("Registration request cannot be null");
        }

        if (!StringUtils.hasText(request.password())) {
            throw new AuthException("Password is required for email registration");
        }

        userRepository.findByEmailAndProvider(request.email(), AuthProvider.EMAIL)
                .ifPresent(existing -> {
                    throw new AuthException("Email is already registered");
                });

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .provider(AuthProvider.EMAIL)
                .username(request.username())
                .role(request.role())
                .providerId(null)
                .build();

        User savedUser = userRepository.save(user);
        String accessToken = jwtUtil.generateToken(savedUser.getId(), 15, "access token", user.getRole().name(),
                user.getEmail(), user.getProvider().name());
        String refreshToken = jwtUtil.generateToken(savedUser.getId(), 129600, "refresh token", user.getRole().name(),
                user.getEmail(), user.getProvider().name());
        saveRefreshToken(savedUser, refreshToken);

        eventPublisher.publishEvent(
                new UserRegistrationEvent(savedUser));

        return createResponse(savedUser.getEmail(), savedUser.getProvider().name(), accessToken, refreshToken);
    }

    private void validateRequest(AuthRequest request) {
        if (!StringUtils.hasText(request.password())) {
            throw new AuthException("Password cannot be blank");
        }
    }

    private AuthResponse createResponse(String email, String provider, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .email(email)
                .provider(provider)
                .message("Authentication successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveRefreshToken(User user, String token) {

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusSeconds(60L * 60 * 24 * 90))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

}
