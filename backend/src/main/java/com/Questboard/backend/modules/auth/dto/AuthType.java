package com.Questboard.backend.modules.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Supported authentication types for the auth module.
 *
 * The enum values follow the more descriptive internal names, and
 * the JSON representation is compatible with the request contract.
 */
public enum AuthType {
    EMAIL_PASSWORD("EMAIL"),
    GOOGLE_OAUTH("GOOGLE");

    private final String jsonValue;

    AuthType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static AuthType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return switch (value.trim().toUpperCase()) {
            case "EMAIL", "EMAIL_PASSWORD" -> EMAIL_PASSWORD;
            case "GOOGLE", "GOOGLE_OAUTH" -> GOOGLE_OAUTH;
            default -> throw new IllegalArgumentException("Unknown authType: " + value);
        };
    }

    public String getStrategyKey() {
        return jsonValue;
    }
}
