package com.Questboard.backend.modules.auth.services;

import com.Questboard.backend.modules.auth.dto.GoogleUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

        @Value("${google.client-id}")
        private String clientId;

        @Value("${google.client-secret}")
        private String clientSecret;

        @Value("${google.redirect-uri}")
        private String redirectUri;

        private final RestTemplate restTemplate = new RestTemplate();

        /**
         * MAIN ENTRY:
         * code -> GoogleUser(email, sub)
         */
        public GoogleUser getUserFromCode(String code) {

                GoogleTokenResponse tokenResponse = exchangeCodeForTokens(code);

                GoogleIdToken.Payload payload = verifyIdToken(tokenResponse.idToken());

                return new GoogleUser(
                                payload.getEmail(),
                                payload.getSubject() // Google unique user ID (sub)
                );
        }

        /**
         * Step 1: Exchange authorization code for tokens
         */
        private GoogleTokenResponse exchangeCodeForTokens(String code) {

                String url = "https://oauth2.googleapis.com/token";

                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("code", code);
                body.add("client_id", clientId);
                body.add("client_secret", clientSecret);
                body.add("redirect_uri", redirectUri);
                body.add("grant_type", "authorization_code");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

                ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
                                url,
                                request,
                                GoogleTokenResponse.class);

                if (!response.getStatusCode().is2xxSuccessful() ||
                                response.getBody() == null) {
                        throw new RuntimeException("Failed to exchange Google code");
                }

                return response.getBody();
        }

        /**
         * Step 2: Verify ID token (IMPORTANT security step)
         */
        private GoogleIdToken.Payload verifyIdToken(String idToken) {

                try {
                        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                                        new NetHttpTransport(),
                                        GsonFactory.getDefaultInstance())
                                        .setAudience(Collections.singletonList(clientId))
                                        .build();

                        GoogleIdToken token = verifier.verify(idToken);

                        if (token == null) {
                                throw new RuntimeException("Invalid Google ID token");
                        }

                        return token.getPayload();

                } catch (Exception e) {
                        throw new RuntimeException("Google token verification failed", e);
                }
        }

        /**
         * DTO for token response
         */
        public record GoogleTokenResponse(

                        @JsonProperty("access_token") String accessToken,

                        @JsonProperty("expires_in") Long expiresIn,

                        @JsonProperty("id_token") String idToken,

                        @JsonProperty("scope") String scope,

                        @JsonProperty("token_type") String tokenType) {
        }
}