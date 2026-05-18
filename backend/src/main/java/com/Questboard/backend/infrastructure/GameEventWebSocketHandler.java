package com.Questboard.backend.infrastructure;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GameEventWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // userId (String) -> session
    private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Object userIdObj = session.getAttributes().get("userId");
        Object emailObj = session.getAttributes().get("email");

        if (userIdObj == null) {
            log.warn("WebSocket rejected: missing userId");
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        UUID userId = (UUID) userIdObj;
        String userIdStr = userId.toString();
        String email = (String) emailObj;

        activeSessions.put(userIdStr, session);

        log.info("User connected via WebSocket: userId={}, email={}", userIdStr, email);
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) throws Exception {

        String payload = message.getPayload();

        try {
            // -----------------------------
            // Get user context
            // -----------------------------
            UUID userId = (UUID) session.getAttributes().get("userId");

            if (userId == null) {
                session.sendMessage(new TextMessage(
                        "{\"status\":\"ERROR\",\"message\":\"Unauthorized\"}"));
                return;
            }

            // -----------------------------
            // Parse event
            // -----------------------------
            GameEventRequest request = objectMapper.readValue(payload, GameEventRequest.class);

            // attach user context
            request.setUserId(userId);

            // -----------------------------
            // Process event (your service)
            // -----------------------------
            // trackingService.processIncomingEvent(request);

            // -----------------------------
            // ACK response
            // -----------------------------
            session.sendMessage(new TextMessage(
                    "{\"status\":\"ACK\"}"));

        } catch (Exception e) {

            log.error("WebSocket error processing payload: {}", payload, e);

            session.sendMessage(new TextMessage(
                    "{\"status\":\"ERROR\",\"message\":\"Invalid payload\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) throws Exception {

        Object userIdObj = session.getAttributes().get("userId");

        if (userIdObj != null) {
            UUID userId = (UUID) userIdObj;
            activeSessions.remove(userId.toString());
        }

        log.info("User disconnected: userId={}, status={}",
                userIdObj, status);
    }

    // ----------------------------------
    // Send message to specific user
    // ----------------------------------
    public void sendToUser(UUID userId, String message) {

        WebSocketSession session = activeSessions.get(userId.toString());

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("Failed to send message to userId={}", userId, e);
            }
        }
    }
}