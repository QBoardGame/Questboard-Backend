package com.Questboard.backend.modules.auth.services;

import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;

/**
 * Auth service boundary for the auth module.
 */
public interface AuthService {

    AuthResponse authenticate(AuthRequest request);

    AuthResponse register(RegisterRequest request);
}
