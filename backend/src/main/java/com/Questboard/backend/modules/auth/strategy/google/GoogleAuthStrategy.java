package com.Questboard.backend.modules.auth.strategy.google;

import com.Questboard.backend.common.JwtUtil;
import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.model.AuthProvider;
import com.Questboard.backend.modules.auth.model.User;
import com.Questboard.backend.modules.auth.repository.UserRepository;
import com.Questboard.backend.modules.auth.services.TokenService;
import com.Questboard.backend.modules.auth.strategy.BaseAuthStrategy;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Authentication flow for Google OAuth.
 */
@Component("GOOGLE")
public class GoogleAuthStrategy extends BaseAuthStrategy {

    private final JwtUtil jwtUtil;

    public GoogleAuthStrategy(UserRepository userRepository, JwtUtil jwtUtil, TokenService tokenService) {
        super(tokenService, userRepository);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        validateRequest(request);

        String providerId = parseProviderId(request.token());
        User user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, providerId)
                .orElseGet(() -> createUser(request.email(), providerId));

        String accessToken = jwtUtil.generateToken(user.getId(), 15, "access token", user.getRole().name(), user.getEmail(), user.getProvider().name(), user.getUsername());
        String refreshToken = jwtUtil.generateToken(user.getId(), 129600, "refresh token", user.getRole().name(), user.getEmail(), user.getProvider().name(), user.getUsername());
        return createResponse(user.getEmail(), user.getProvider().name(), accessToken, refreshToken);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        String providerId = parseProviderId(request.token());
        User user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, providerId)
                .orElseGet(() -> createUser(request.email(), providerId));

        String accessToken = jwtUtil.generateToken(user.getId(), 15, "access token", user.getRole().name(), user.getEmail(), user.getProvider().name(), user.getUsername());
        String refreshToken = jwtUtil.generateToken(user.getId(), 129600, "refresh token", user.getRole().name(), user.getEmail(), user.getProvider().name(), user.getUsername());
        return createResponse(user.getEmail(), user.getProvider().name(), accessToken, refreshToken);
    }

    private void validateRequest(AuthRequest request) {
        if (!StringUtils.hasText(request.token())) {
            throw new AuthException("Token is required for GOOGLE auth");
        }

        if (!StringUtils.hasText(request.email())) {
            throw new AuthException("Email is required for GOOGLE auth");
        }
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (!StringUtils.hasText(request.token())) {
            throw new AuthException("Token is required for GOOGLE registration");
        }

        if (!StringUtils.hasText(request.email())) {
            throw new AuthException("Email is required for GOOGLE registration");
        }
    }

    private String parseProviderId(String token) {
        if (!token.startsWith("GOOGLE_")) {
            throw new AuthException("Invalid Google token format");
        }

        return token.substring("GOOGLE_".length());
    }

    private User createUser(String email, String providerId) {

    User user = User.builder()
                .email(email)
                .provider(AuthProvider.EMAIL)
                // .username(request.getUsername())
                .providerId(null)
                .build();

    return userRepository.save(user);
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
