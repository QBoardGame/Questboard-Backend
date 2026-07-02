package com.Questboard.backend.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(

        @NotBlank
        String resetId,

        @NotBlank
        String newPassword,

        @NotBlank
        String confirmPassword

) {}