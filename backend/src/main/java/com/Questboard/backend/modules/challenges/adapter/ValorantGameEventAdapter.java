package com.Questboard.backend.modules.challenges.adapter;

import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValorantGameEventAdapter implements GameEventAdapter {

    private static final Long VALORANT_GAME_ID = 21640L;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Long supportedGameId() {
        return VALORANT_GAME_ID;
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
}
