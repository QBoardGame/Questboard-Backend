package com.Questboard.backend.modules.auth.strategy.local;

import com.Questboard.backend.common.RedisService;
import com.Questboard.backend.config.EmailService;
import com.Questboard.backend.modules.auth.dto.TokenPair;
import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.request.ResetPasswordRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.events.UserRegistrationEvent;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.model.AuthProvider;
import com.Questboard.backend.modules.auth.model.PolicyAcceptance;
import com.Questboard.backend.modules.auth.model.User;
import com.Questboard.backend.modules.auth.repository.PolicyAcceptanceRepository;
import com.Questboard.backend.modules.auth.repository.UserRepository;
import com.Questboard.backend.modules.auth.services.TokenService;
import com.Questboard.backend.modules.auth.strategy.BaseAuthStrategy;
import com.Questboard.backend.modules.auth.strategy.PasswordResetCapable;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

/**
 * Authentication flow for email / password credentials.
 */
@Component("EMAIL")
@Slf4j
public class EmailPasswordAuthStrategy extends BaseAuthStrategy implements PasswordResetCapable {

    @Value("${password-resert.url}")
    private String passwordResetUrl;

    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisService redisService;
    private final EmailService emailService;
    private final PolicyAcceptanceRepository policyAcceptanceRepository;

    public EmailPasswordAuthStrategy(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher,
            TokenService tokenService,
            RedisService redisService,
            EmailService emailService,
            PolicyAcceptanceRepository policyAcceptanceRepository) {

        super(tokenService, userRepository);
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.redisService = redisService;
        this.emailService = emailService;
        this.policyAcceptanceRepository = policyAcceptanceRepository;
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

        PolicyAcceptance acceptance = PolicyAcceptance.builder()
                .user(savedUser)
                .termsAccepted(request.acceptedTerms())
                .privacyAccepted(request.acceptedTerms()) // same checkbox accepts both
                .build();

        policyAcceptanceRepository.save(acceptance);
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

    @Override
    public ResponseEntity<?> sendPasswordResetLink(String email) {

        if (email == null || email.trim().isEmpty()) {
            log.warn("❌ Password reset request failed: Missing email");
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Email is required"));
        }

        log.info("🔐 Password reset request received for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        // String token = passwordResetTokenService.createToken(user);

        String token = tokenService.passwordResetToken(user);
        String resetId = UUID.randomUUID().toString();
        redisService.setToken("reset", resetId, token, 15 * 60);

        log.info("✅ Reset token generated and stored in Redis for user: {}", user.getUsername());

        String resetLink = passwordResetUrl + "/reset-password/" + resetId;
        String emailBody = String.format("""
                Click the link below to reset your password:
                %s

                This link will expire in 15 minutes.
                """, resetLink);

        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                emailBody);

        log.info("📧 Password reset email sent to: {}", user.getEmail());

        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset link sent!"));
    }

    @Override
    @Transactional
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {

        String resetId = request.resetId();
        String newPassword = request.newPassword();
        String confirmPassword = request.confirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            log.warn("❌ Password reset failed: Passwords do not match");
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        log.info("🔐 Received password reset request for resetId: {}", resetId);

        String token = redisService.getToken("reset", resetId);
        if (token == null) {
            log.warn("❌ No token found in Redis for resetId: {}", resetId);
            return ResponseEntity.badRequest().body("Invalid or expired reset link");
        }

        UUID userId;
        try {
            userId = tokenService.validateTokenAndGetUserId(token);
        } catch (Exception e) {
            log.error("❌ Failed to extract userId from token for resetId {}: {}", resetId, e.getMessage());
            return ResponseEntity.badRequest().body("Invalid token format");
        }

        log.info("✅ Extracted userId '{}' from token", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("❌ User not found in database: {}", userId);
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
                });

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            log.warn("⚠️ User '{}' tried resetting to the current password", userId);
            return ResponseEntity.badRequest().body("New password cannot be same as old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redisService.deleteToken("reset", resetId);

        log.info("✅ Password reset successful for user: {}", userId);

        return ResponseEntity.ok("Password reset successful");
    }
}
