package com.Questboard.backend.modules.auth.dto.request;

import com.Questboard.backend.modules.auth.dto.AuthType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for authentication flows.
 */
public record AuthRequest(

    @NotNull(message = "authType is required")
    AuthType authType,

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    String email,

    String password,

    String token

) {}