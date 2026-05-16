package com.Questboard.backend.modules.auth.services.factory;

import com.Questboard.backend.modules.auth.dto.AuthType;
import com.Questboard.backend.modules.auth.exception.AuthException;
import com.Questboard.backend.modules.auth.services.strategy.AuthStrategy;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Factory that resolves authentication strategies by auth type.
 */
@Component
public class AuthStrategyFactory {

    private final Map<String, AuthStrategy> strategies;

    public AuthStrategyFactory(Map<String, AuthStrategy> strategies) {
        this.strategies = strategies;
    }

    public AuthStrategy getStrategy(AuthType authType) {
        if (authType == null) {
            throw new AuthException("authType cannot be null");
        }

        AuthStrategy strategy = strategies.get(authType.getStrategyKey());
        if (strategy == null) {
            throw new AuthException("Unsupported authType: " + authType);
        }

        return strategy;
    }
}
