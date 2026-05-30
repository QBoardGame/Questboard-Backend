package com.Questboard.backend.modules.challenges.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipationResponse {

    private UUID challengeId;
    private UUID userId;
    private String status;
    private Instant joinedAt;
    private Long currentParticipants;
    private Long maxParticipants;

    public static ParticipationResponse alreadyJoined(UUID challengeId, UUID userId) {
        return ParticipationResponse.builder()
                .challengeId(challengeId)
                .userId(userId)
                .status("ALREADY_JOINED")
                .build();
    }
}
