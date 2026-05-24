package com.Questboard.backend.modules.auth.model;

/**
 * Represents user roles in the system.
 */
public enum Role {
    USER,
    ADMIN,
    MODERATOR,
    BRAND,
    STREAMER,
    CREATOR;

    public static Role fromString(String role) {
        if (role == null || role.isBlank()) {
            return USER;
        }

        return switch (role.toUpperCase()) {
            case "ADMIN" -> ADMIN;
            case "MODERATOR" -> MODERATOR;
            case "BRAND" -> BRAND;
            case "STREAMER" -> STREAMER;
            case "CREATOR" -> CREATOR;
            default -> USER;
        };
    }
}