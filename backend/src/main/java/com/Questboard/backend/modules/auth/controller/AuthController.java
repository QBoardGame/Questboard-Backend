package com.Questboard.backend.modules.auth.controller;

import com.Questboard.backend.modules.auth.dto.AuthType;
import com.Questboard.backend.modules.auth.dto.GoogleUser;
import com.Questboard.backend.modules.auth.dto.JwtUserPrincipal;
import com.Questboard.backend.modules.auth.dto.request.AuthRequest;
import com.Questboard.backend.modules.auth.dto.request.ForgotPasswordRequest;
import com.Questboard.backend.modules.auth.dto.request.RefreshTokenRequest;
import com.Questboard.backend.modules.auth.dto.request.RegisterRequest;
import com.Questboard.backend.modules.auth.dto.request.ResetPasswordRequest;
import com.Questboard.backend.modules.auth.dto.response.AuthMeResponse;
import com.Questboard.backend.modules.auth.dto.response.AuthResponse;
import com.Questboard.backend.modules.auth.services.AuthService;
import com.Questboard.backend.modules.auth.services.GoogleOAuthService;
import com.Questboard.backend.modules.auth.services.TokenService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for authentication flows.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

    private final AuthService authService;
    private final TokenService tokenService;
    private final GoogleOAuthService googleOAuthService;

    public AuthController(AuthService authService, TokenService tokenService,
            GoogleOAuthService googleOAuthService) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.googleOAuthService = googleOAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.authenticate(authRequest);

        return ResponseEntity.ok()
                .body(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);

        return ResponseEntity.ok()
                .body(authResponse);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return tokenService.refreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal JwtUserPrincipal principal) {

        tokenService.logout(principal.getUserId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthMeResponse> me(
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        AuthMeResponse response = authService.getCurrentUserInfo(principal);

        System.out.println("AuthMeResponse: " + response);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/google/start")
    public void startGoogleAuth(
            @RequestParam String redirect,
            HttpServletResponse response) throws IOException {

        String state = Base64.getUrlEncoder()
                .encodeToString(redirect.getBytes());

        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + googleRedirectUri
                + "&response_type=code"
                + "&scope=openid%20email%20profile"
                + "&state=" + state
                + "&prompt=select_account";

        response.sendRedirect(url);
    }

    @GetMapping("/google/callback")
    public void callback(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletResponse response) throws Exception {

        // String redirectUri = new String(Base64.getUrlDecoder().decode(state));

        // 1. Google → user info
        GoogleUser googleUser = googleOAuthService.getUserFromCode(code);

        // 2. Build your system request
        AuthRequest authRequest = AuthRequest.builder()
                .authType(AuthType.GOOGLE_OAUTH)
                .email(googleUser.email())
                .token(googleUser.providerId())
                .build();

        // 3. Delegate to YOUR auth system
        AuthResponse authResponse = authService.authenticate(authRequest);

        // 4. Redirect back to Overwolf
        response.sendRedirect(
                // redirectUri
                "overwolf-extension://auth-success"
                        + "?accessToken=" + authResponse.accessToken()
                        + "&refreshToken=" + authResponse.refreshToken());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request) {

        authService.sendPasswordResetLink(request.email());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok().build();
    }
}