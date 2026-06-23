package com.Questboard.backend.modules.auth.strategy.google;

import com.Questboard.backend.modules.auth.dto.TokenPair;
import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.model.AuthProvider;
import com.Questboard.backend.modules.auth.model.Role;
import com.Questboard.backend.modules.auth.model.User;
import com.Questboard.backend.modules.auth.repository.UserRepository;
import com.Questboard.backend.modules.auth.services.TokenService;
import com.Questboard.backend.modules.auth.strategy.AuthStrategy;
import com.Questboard.backend.modules.auth.strategy.BaseAuthStrategy;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Authentication flow for Google OAuth.
 */
// @Component("GOOGLE")
// public class GoogleAuthStrategy extends BaseAuthStrategy {

// private final JwtUtil jwtUtil;

// public GoogleAuthStrategy(UserRepository userRepository, JwtUtil jwtUtil,
// TokenService tokenService) {
// super(tokenService, userRepository);
// this.jwtUtil = jwtUtil;
// }

// @Override
// public AuthResponse authenticate(AuthRequest request) {
// validateRequest(request);

// String providerId = parseProviderId(request.token());
// User user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE,
// providerId)
// .orElseGet(() -> createUser(request.email(), providerId));

// String accessToken = jwtUtil.generateToken(user.getId(), 15, "access token",
// user.getRole().name(), user.getEmail(), user.getProvider().name(),
// user.getUsername());
// String refreshToken = jwtUtil.generateToken(user.getId(), 129600, "refresh
// token", user.getRole().name(), user.getEmail(), user.getProvider().name(),
// user.getUsername());
// return createResponse(user.getEmail(), user.getProvider().name(),
// accessToken, refreshToken);
// }

// @Override
// public AuthResponse register(RegisterRequest request) {
// validateRegisterRequest(request);

// String providerId = parseProviderId(request.token());
// User user = userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE,
// providerId)
// .orElseGet(() -> createUser(request.email(), providerId));

// String accessToken = jwtUtil.generateToken(user.getId(), 15, "access token",
// user.getRole().name(), user.getEmail(), user.getProvider().name(),
// user.getUsername());
// String refreshToken = jwtUtil.generateToken(user.getId(), 129600, "refresh
// token", user.getRole().name(), user.getEmail(), user.getProvider().name(),
// user.getUsername());
// return createResponse(user.getEmail(), user.getProvider().name(),
// accessToken, refreshToken);
// }

// private void validateRequest(AuthRequest request) {
// if (!StringUtils.hasText(request.token())) {
// throw new AuthException("Token is required for GOOGLE auth");
// }

// if (!StringUtils.hasText(request.email())) {
// throw new AuthException("Email is required for GOOGLE auth");
// }
// }

// private void validateRegisterRequest(RegisterRequest request) {
// if (!StringUtils.hasText(request.token())) {
// throw new AuthException("Token is required for GOOGLE registration");
// }

// if (!StringUtils.hasText(request.email())) {
// throw new AuthException("Email is required for GOOGLE registration");
// }
// }

// private String parseProviderId(String token) {
// if (!token.startsWith("GOOGLE_")) {
// throw new AuthException("Invalid Google token format");
// }

// return token.substring("GOOGLE_".length());
// }

// private User createUser(String email, String providerId) {

// User user = User.builder()
// .email(email)
// .provider(AuthProvider.EMAIL)
// // .username(request.getUsername())
// .providerId(null)
// .build();

// return userRepository.save(user);
// }

// private AuthResponse createResponse(String email, String provider, String
// accessToken, String refreshToken) {
// return AuthResponse.builder()
// .email(email)
// .provider(provider)
// .message("Authentication successful")
// .accessToken(accessToken)
// .refreshToken(refreshToken)
// .build();
// }
// }

@Component("GOOGLE")
public class GoogleAuthStrategy extends BaseAuthStrategy {

    public GoogleAuthStrategy(UserRepository userRepository,
            TokenService tokenService) {
        super(tokenService, userRepository);
    }

    // @Override
    // @Transactional
    // public AuthResponse authenticate(AuthRequest request) {
    // validateRequest(request);

    // String providerId = parseProviderId(request.token());

    // // 1. Always resolve by email (identity-first approach)
    // User user = userRepository.findByEmail(request.email())
    // .orElseGet(() -> createUser(request.email(), providerId));

    // // 2. Link Google if not already linked
    // if (user.getProviderId() == null) {
    // user.setProvider(AuthProvider.GOOGLE);
    // user.setProviderId(providerId);
    // userRepository.save(user);
    // }

    // // 3. Generate tokens (same as email/password flow)
    // return generateResponse(user);
    // }

    // @Override
    // @Transactional
    // public AuthResponse authenticate(AuthRequest request) {

    // validateRequest(request);

    // String providerId = request.token();

    // User user = userRepository.findByEmail(request.email())
    // .orElseGet(() -> createUser(request.email(), providerId));

    // if (user.getProviderId() == null) {
    // user.setProvider(AuthProvider.GOOGLE);
    // user.setProviderId(providerId);
    // userRepository.save(user);
    // }

    // return generateResponse(user);
    // }

    // @Override
    // @Transactional
    // public AuthResponse register(RegisterRequest request) {
    // validateRegisterRequest(request);

    // String providerId = parseProviderId(request.token());

    // User user = userRepository.findByEmail(request.email())
    // .orElseGet(() -> createUser(request.email(), providerId));

    // if (user.getProviderId() == null) {
    // user.setProvider(AuthProvider.GOOGLE);
    // user.setProviderId(providerId);
    // userRepository.save(user);
    // }

    // return generateResponse(user);
    // }

    // private User createUser(String email, String providerId) {
    // User user = User.builder()
    // .email(email)
    // .username(email.split("@")[0])
    // .provider(AuthProvider.GOOGLE)
    // .providerId(providerId)
    // .password(null)
    // .role(Role.USER) // default safe value
    // .isVerified(true)
    // .build();

    // return userRepository.save(user);
    // }

    // private AuthResponse generateResponse(User user) {

    // TokenPair tokens = tokenService.createTokens(user);

    // return AuthResponse.builder()
    // .email(user.getEmail())
    // .provider(user.getProvider().name())
    // .message("Authentication successful")
    // .accessToken(tokens.accessToken())
    // .refreshToken(tokens.refreshToken())
    // .build();
    // }

    // private void validateRequest(AuthRequest request) {
    // if (!StringUtils.hasText(request.token())) {
    // throw new AuthException("Token is required for GOOGLE auth");
    // }
    // if (!StringUtils.hasText(request.email())) {
    // throw new AuthException("Email is required for GOOGLE auth");
    // }
    // }

    // private void validateRegisterRequest(RegisterRequest request) {
    // if (!StringUtils.hasText(request.token())) {
    // throw new AuthException("Token is required for GOOGLE registration");
    // }
    // if (!StringUtils.hasText(request.email())) {
    // throw new AuthException("Email is required for GOOGLE registration");
    // }
    // }

    // private String parseProviderId(String token) {
    // if (!token.startsWith("GOOGLE_")) {
    // throw new AuthException("Invalid Google token format");
    // }
    // return token.substring("GOOGLE_".length());
    // }

    // @Component("GOOGLE")
    // @RequiredArgsConstructor
    // public class GoogleAuthStrategy implements AuthStrategy {

    // private final UserRepository userRepository;
    // private final TokenService tokenService;

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {

        String email = request.email();
        String providerId = request.token(); // Google "sub"

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, providerId));

        if (user.getProviderId() == null) {
            user.setProvider(AuthProvider.GOOGLE);
            user.setProviderId(providerId);
            userRepository.save(user);
        }

        return generateResponse(user);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        throw new UnsupportedOperationException("Google uses OAuth login flow only");
    }

    private User createUser(String email, String providerId) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .username(email.split("@")[0])
                        .provider(AuthProvider.GOOGLE)
                        .providerId(providerId)
                        .role(Role.USER)
                        .isVerified(true)
                        .build());
    }

    private AuthResponse generateResponse(User user) {
        TokenPair tokens = tokenService.createTokens(user);

        return AuthResponse.builder()
                .email(user.getEmail())
                .provider(user.getProvider().name())
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .message("Google authentication successful")
                .build();
    }

}