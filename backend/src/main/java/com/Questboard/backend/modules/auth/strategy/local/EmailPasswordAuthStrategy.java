package com.Questboard.backend.modules.auth.strategy.local;

import com.Questboard.backend.modules.auth.dto.TokenPair;
import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.events.UserRegistrationEvent;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.model.AuthProvider;
import com.Questboard.backend.modules.auth.model.User;
import com.Questboard.backend.modules.auth.repository.UserRepository;
import com.Questboard.backend.modules.auth.services.TokenService;
import com.Questboard.backend.modules.auth.strategy.BaseAuthStrategy;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Authentication flow for email / password credentials.
 */
@Component("EMAIL")
public class EmailPasswordAuthStrategy extends BaseAuthStrategy {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public EmailPasswordAuthStrategy(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher,
            TokenService tokenService) {

        super(tokenService, userRepository);
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        validateRequest(request);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (user.getPassword() == null) {
            throw new AuthException("Please login with Google");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        TokenPair tokens = tokenService.createTokens(user);
        return createResponse(user.getEmail(), user.getProvider().name(), tokens.accessToken(), tokens.refreshToken());
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
        TokenPair tokens = tokenService.createTokens(savedUser);

        eventPublisher.publishEvent(
                new UserRegistrationEvent(savedUser));

        return createResponse(savedUser.getEmail(), savedUser.getProvider().name(), tokens.accessToken(),
                tokens.refreshToken());
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

}
