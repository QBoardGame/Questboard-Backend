package com.Questboard.backend.infrastructure;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.Questboard.backend.common.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(
            JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest)) {
            return true;
        }

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

        // ✅ Overwolf / desktop safe
        String accessToken = servletRequest.getParameter("access_token");

        if (accessToken == null || accessToken.isEmpty()) {
            attributes.put("isGuest", true);
            return true;
        }

        try {
            // -----------------------------
            // BASIC TOKEN VALIDATION
            // -----------------------------

            if (!jwtUtil.isAccessToken(accessToken)) {
                return false;
            }

            if (!jwtUtil.isTokenValid(accessToken)) {
                return false;
            }

            String userIdStr = jwtUtil.extractUserId(accessToken);
            String email = jwtUtil.extractEmail(accessToken);
            String role = jwtUtil.extractRole(accessToken);

            UUID userId = UUID.fromString(userIdStr);

            // Load user (ensures user still exists / not deleted / not disabled)
            // JwtUserPrincipal principal = new JwtUserPrincipal(userId, email, role);
            // -----------------------------
            // ATTACH CONTEXT TO SOCKET
            // -----------------------------

            attributes.put("userId", userId);
            attributes.put("email", email);
            attributes.put("role", role);
            attributes.put("access_token", accessToken);
            attributes.put("isGuest", false);

            return true;

        } catch (Exception e) {
            // attributes.put("isGuest", true);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // no-op
    }
}