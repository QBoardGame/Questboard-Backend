package com.Questboard.backend.modules.auth.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserPrincipal {

    private UUID userId;
    private String email;
    private String role;
    private String username;
}