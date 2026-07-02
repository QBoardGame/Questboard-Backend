package com.Questboard.backend.modules.auth.services.impl;

import com.Questboard.backend.modules.auth.dto.AuthType;
import com.Questboard.backend.modules.auth.dto.JwtUserPrincipal;
import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.request.ResetPasswordRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthMeResponse;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.services.AuthService;
import com.Questboard.backend.modules.auth.strategy.AuthStrategy;
import com.Questboard.backend.modules.auth.strategy.PasswordResetCapable;
import com.Questboard.backend.modules.auth.strategy.factory.AuthStrategyFactory;
import com.Questboard.backend.modules.wallet.dto.WalletStatsDto;
import com.Questboard.backend.modules.wallet.service.WalletService;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.auth.AUTH;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

/**
 * Primary implementation of auth service.
 * Delegates work to provider-specific authentication strategies.
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthStrategyFactory authStrategyFactory;
    private final WalletService walletService;

    public AuthServiceImpl(AuthStrategyFactory authStrategyFactory, WalletService walletService) {
        this.authStrategyFactory = authStrategyFactory;
        this.walletService = walletService;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        if (request == null) {
            throw new AuthException("Authentication request cannot be null");
        }

        AuthStrategy strategy = authStrategyFactory.getStrategy(request.authType());
        return strategy.authenticate(request);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (request == null) {
            throw new AuthException("Registration request cannot be null");
        }

        AuthStrategy strategy = authStrategyFactory.getStrategy(request.authType());
        return strategy.register(request);
    }

    public AuthMeResponse getCurrentUserInfo(@AuthenticationPrincipal JwtUserPrincipal principal) {
        // log.info("AUTH ME called for userID={}", principal.getUserId());
        if (principal == null) {
            return null;
        }
        log.info("AUTH ME called for: userId={}", principal.getUserId());

        WalletStatsDto walletStats = walletService.getWalletStats(principal.getUserId());

        return AuthMeResponse.builder()
                .userId(principal.getUserId())
                .username(principal.getUsername())
                .email(principal.getEmail())
                .role(principal.getRole())
                .wallet(walletStats)
                .build();
    }

    @Override
    public void sendPasswordResetLink(String email) {
        getPasswordResetStrategy().sendPasswordResetLink(email);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        getPasswordResetStrategy().resetPassword(request);
    }

    private PasswordResetCapable getPasswordResetStrategy() {
        AuthStrategy strategy = authStrategyFactory.getStrategy(AuthType.EMAIL_PASSWORD);

        if (!(strategy instanceof PasswordResetCapable resetStrategy)) {
            throw new AuthException("Password reset not supported for this provider");
        }

        return resetStrategy;
    }
}
