package com.Questboard.backend.modules.auth.strategy;

import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;

/**
 * Strategy contract for authentication providers.
 */
public interface AuthStrategy {

    AuthResponse authenticate(AuthRequest request);

    AuthResponse register(RegisterRequest request);
}
