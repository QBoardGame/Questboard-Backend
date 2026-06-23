package com.Questboard.backend.modules.challenges.adapter;

import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.enums.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounterStrike2GameEventAdapter implements GameEventAdapter {

    private static final Long COUNTER_STRIKE_2_GAME_ID = 22730L;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Long supportedGameId() {
        return COUNTER_STRIKE_2_GAME_ID;
    }

    @Override
    public JsonNode normalize(GameEventDto event) {

        ObjectNode normalized = objectMapper.createObjectNode();

        normalized.put("gameId", event.getGameId());

        normalized.put(
                "eventType",
                event.getEventType() != null
                        ? event.getEventType().name()
                        : null);

        normalized.put(
                "value",
                event.getValue() != null
                        ? event.getValue()
                        : 0L);

        normalized.put(
                "count",
                event.getCount() != null
                        ? event.getCount()
                        : 0L);

        if (event.getMetadata() != null
                && event.getMetadata().isObject()) {

            ObjectNode metadata =
                    (ObjectNode) event.getMetadata().deepCopy();

            normalizeMetadata(metadata);

            metadata.properties()
                    .forEach(entry ->
                            normalized.set(
                                    entry.getKey(),
                                    entry.getValue()));
        }

        return normalized;
    }

    private void normalizeMetadata(ObjectNode metadata) {

        normalizeWeapon(metadata);

        normalizeMap(metadata);
    }

    private void normalizeWeapon(ObjectNode metadata) {

        JsonNode weaponNode = metadata.get("weapon");

        if (weaponNode == null) {
            return;
        }

        String weapon = weaponNode.asText();

        metadata.put(
                "weapon",
                weapon
                        .replace("weapon_", "")
                        .toUpperCase());
    }

    private void normalizeMap(ObjectNode metadata) {

        JsonNode mapNode = metadata.get("map");

        if (mapNode == null) {
            return;
        }

        String map = mapNode.asText();

        metadata.put(
                "map",
                map
                        .replace("de_", "")
                        .replace("cs_", "")
                        .toUpperCase());
    }

    @Override
    public long calculateProgress(
            UserChallengeProgress progress,
            GameEventDto event,
            JsonNode normalizedEvent) {

        boolean isHeadshot =
                normalizedEvent.path("headshot").asBoolean(false);

        Set<EventType> applicableEvents =
                resolveEventTypes(
                        event.getEventType(),
                        isHeadshot);

        if (!applicableEvents.contains(progress.getEventType())) {
            return 0;
        }

        return event.getValue() != null
                ? event.getValue()
                : 1L;
    }

    private Set<EventType> resolveEventTypes(
            EventType incomingEvent,
            boolean isHeadshot) {

        Set<EventType> events = EnumSet.of(incomingEvent);

        if (incomingEvent == EventType.KILL && isHeadshot) {
            events.add(EventType.HEADSHOT);
        }

        return events;
    }
}