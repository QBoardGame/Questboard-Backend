package com.Questboard.backend.modules.auth.services.impl;

import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.services.AuthService;
import com.Questboard.backend.modules.auth.strategy.AuthStrategy;
import com.Questboard.backend.modules.auth.strategy.factory.AuthStrategyFactory;

import org.springframework.stereotype.Service;

/**
 * Primary implementation of auth service.
 * Delegates work to provider-specific authentication strategies.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthStrategyFactory authStrategyFactory;

    public AuthServiceImpl(AuthStrategyFactory authStrategyFactory) {
        this.authStrategyFactory = authStrategyFactory;
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
}
