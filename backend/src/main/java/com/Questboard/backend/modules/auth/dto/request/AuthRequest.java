package com.Questboard.backend.modules.auth.dto.request;

import com.Questboard.backend.modules.auth.dto.AuthType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AuthRequest(

        @NotNull(message = "authType is required") AuthType authType,

        String email,

        String password,

        String token

) {

    @AssertTrue(message = "Invalid authentication payload")
    public boolean isValidAuthRequest() {

        if (authType == AuthType.EMAIL_PASSWORD) {
            return email != null
                    && !email.isBlank()
                    && password != null
                    && !password.isBlank()
                    && (token == null || token.isBlank());
        }

        if (authType == AuthType.GOOGLE_OAUTH) {
            return token != null
                    && !token.isBlank()
                    && (email == null || email.isBlank())
                    && (password == null || password.isBlank());
        }

        return false;
    }
}