package com.Questboard.backend.modules.challenges.adapter;

import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.fasterxml.jackson.databind.JsonNode;

public interface GameEventAdapter {
    Long supportedGameId();
    JsonNode normalize(GameEventDto event);
    long calculateProgress(
            UserChallengeProgress progress,
            GameEventDto event,
            JsonNode normalizedEvent);
}
