package com.Questboard.backend.modules.challenges.adapter;

import org.springframework.stereotype.Component;

import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.enums.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OverwatchGameEventAdapter implements GameEventAdapter {

    private static final Long OVERWATCH_GAME_ID = 10844L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Long supportedGameId() {
        return OVERWATCH_GAME_ID;
    }

    @Override
    public JsonNode normalize(GameEventDto event) {
        ObjectNode normalized = objectMapper.createObjectNode();

        normalized.put("gameId", event.getGameId());
        normalized.put("eventType", event.getEventType() != null ? event.getEventType().name() : null);
        normalized.put("value", event.getValue() != null ? event.getValue() : 0L);
        normalized.put("count", event.getCount() != null ? event.getCount() : 0L);

        if (event.getMetadata() != null && event.getMetadata().isObject()) {
            event.getMetadata().properties().forEach(entry -> normalized.set(entry.getKey(), entry.getValue()));
        }

        return normalized;
    }

    @Override
    public long calculateProgress(
            UserChallengeProgress progress,
            GameEventDto event,
            JsonNode normalizedEvent) {

        EventType incomingEvent = event.getEventType();

        if (incomingEvent == null) {
            return 0;
        }

        if (!incomingEvent.equals(progress.getEventType())) {
            return 0;
        }

        return event.getValue() != null
                ? event.getValue()
                : 1L;
    }
}
