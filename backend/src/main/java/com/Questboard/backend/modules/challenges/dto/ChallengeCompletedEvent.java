package com.Questboard.backend.modules.challenges.dto;

import java.util.UUID;

public record ChallengeCompletedEvent(
        UUID userId,
        UUID challengeId,
        Long gameId,
        UUID progressId,
        String rewardAmount
) {}
