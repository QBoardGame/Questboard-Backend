package com.Questboard.backend.infrastructure;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.Questboard.backend.modules.challenges.dto.EventProcessingResult;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.enums.EventType;
import com.Questboard.backend.modules.challenges.service.GameEventService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameEventWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GameEventService gameEventService;

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
        System.out.println("WebSocket message received: " + payload);

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
            WebSocketEnvelope envelope = objectMapper.readValue(payload, WebSocketEnvelope.class);

            GameEventRequest request = envelope.getPayload();

            System.out.println("DESERIALIZED REQUEST = " + request);
            // attach user context
            request.setUserId(userId);

            // -----------------------------
            // Process event (your service)
            // -----------------------------

            GameEventDto dto = toDto(request);

            // UUID eventId = gameEventService.submitEvent(
            // dto,
            // userId);

            List<EventProcessingResult> results = gameEventService.submitEvent(dto, userId);

            sendToUser(
                    userId,
                    objectMapper.writeValueAsString(
                            Map.of(
                                    "type", "progress_update",
                                    "updates", results)));

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

        if (userIdObj instanceof UUID userId) {

            activeSessions.computeIfPresent(
                    userId.toString(),
                    (key, currentSession) -> currentSession.getId().equals(session.getId())
                            ? null
                            : currentSession);
        }

        log.info("User disconnected: userId={}, status={}",
                userIdObj, status);

        super.afterConnectionClosed(session, status);
    }

    public void disconnectUser(UUID userId) {

        WebSocketSession session = activeSessions.get(userId.toString());

        if (session != null && session.isOpen()) {
            try {
                session.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                log.error("Failed to close websocket for user {}", userId, e);
            }
        }
    }

    // ----------------------------------
    // Send message to specific user
    // ----------------------------------
    public void sendToUser(UUID userId, String message) {
        log.info("Sending message to userId={}, => message={}", userId, message);

        WebSocketSession session = activeSessions.get(userId.toString());

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("Failed to send message to userId={}", userId, e);
            }
        }
    }

    private GameEventDto toDto(GameEventRequest request) {

        System.out.println("========== REQUEST ==========");
        System.out.println("gameId     = " + request.getGameId());
        System.out.println("eventType  = " + request.getEventType());
        System.out.println("count      = " + request.getCount());
        System.out.println("metadata   = " + request.getMetadata());
        System.out.println("=============================");

        return GameEventDto.builder()
                .gameId(request.getGameId())
                .eventType(EventType.valueOf(request.getEventType()))
                .count(request.getCount())
                .metadata(objectMapper.valueToTree(request.getMetadata()))
                .value(
                        request.getCount() == null
                                ? 1L
                                : request.getCount().longValue())
                .build();
    }
}