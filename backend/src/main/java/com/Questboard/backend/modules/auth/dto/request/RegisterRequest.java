package com.Questboard.backend.modules.auth.dto.request;

import com.Questboard.backend.modules.auth.dto.AuthType;
import com.Questboard.backend.modules.auth.model.Role;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for new account registration.
 */
public record RegisterRequest(

                @NotNull(message = "Authentication type is required") AuthType authType,

                @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") @Size(max = 255, message = "Email cannot exceed 255 characters") String email,

                @NotBlank(message = "Username is required") @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters") @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Username can only contain letters, numbers, dots, and underscores") String username,

                @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one number") String password,

                @Size(max = 2048, message = "Token length is invalid") String token,

                @AssertTrue(message = "Please accept the Terms of Service and Privacy Policy") boolean acceptedTerms,
                Role role

) {
}