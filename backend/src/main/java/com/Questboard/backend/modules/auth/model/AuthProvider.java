package com.Questboard.backend.modules.auth.model;

import com.Questboard.backend.modules.auth.dto.AuthType;

/**
 * Represents the provider used to authenticate a user.
 */
public enum AuthProvider {
    EMAIL,
    GOOGLE;

    public static AuthProvider fromAuthType(AuthType authType) {
        return switch (authType) {
            case EMAIL_PASSWORD -> EMAIL;
            case GOOGLE_OAUTH -> GOOGLE;
        };
    }
}
