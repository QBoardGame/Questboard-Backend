package com.Questboard.backend.infrastructure;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.HttpStatus;

import com.Questboard.backend.common.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        log.info("=== WebSocket Handshake Started ===");

        if (!(request instanceof ServletServerHttpRequest servletRequestWrapper)) {
            log.warn("Request is not ServletServerHttpRequest");
            return true;
        }

        HttpServletRequest servletRequest = servletRequestWrapper.getServletRequest();

        log.info("Request URI: {}", servletRequest.getRequestURI());
        log.info("Query String: {}", servletRequest.getQueryString());
        log.info("Remote Addr: {}", servletRequest.getRemoteAddr());
        log.info("Origin: {}", servletRequest.getHeader("Origin"));

        String accessToken = servletRequest.getParameter("access_token");

        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("No access token provided. Connecting as guest.");

            attributes.put("isGuest", true);

            return true;
        }

        try {

            log.info("Access token received");

            // -----------------------------
            // TOKEN TYPE CHECK
            // -----------------------------
            boolean isAccessToken = jwtUtil.isAccessToken(accessToken);

            log.info("isAccessToken: {}", isAccessToken);

            if (!isAccessToken) {
                log.error("Token is NOT an access token");

                response.setStatusCode(HttpStatus.UNAUTHORIZED);

                return false;
            }

            // -----------------------------
            // TOKEN VALIDATION
            // -----------------------------
            boolean isValid = jwtUtil.isTokenValid(accessToken);

            if (!isValid) {
                log.error("Token validation failed");

                response.setStatusCode(HttpStatus.UNAUTHORIZED);

                return false;
            }

            // -----------------------------
            // EXTRACT USER DETAILS
            // -----------------------------
            String userIdStr = jwtUtil.extractUserId(accessToken);
            String email = jwtUtil.extractEmail(accessToken);
            String role = jwtUtil.extractRole(accessToken);

            log.info("Extracted userId: {}", userIdStr);
            log.info("Extracted email: {}", email);
            log.info("Extracted role: {}", role);

            UUID userId = UUID.fromString(userIdStr);

            // -----------------------------
            // ATTACH CONTEXT
            // -----------------------------
            attributes.put("userId", userId);
            attributes.put("email", email);
            attributes.put("role", role);
            attributes.put("access_token", accessToken);
            attributes.put("isGuest", false);

            log.info("WebSocket authentication successful for user: {}", email);

            return true;

        } catch (Exception e) {

            log.error("WebSocket authentication failed");

            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {

        if (exception != null) {
            log.error("After handshake exception", exception);
        } else {
            log.info("WebSocket handshake completed");
        }
    }
}