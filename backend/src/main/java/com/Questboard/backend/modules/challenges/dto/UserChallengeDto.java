package com.Questboard.backend.modules.challenges.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallengeDto {
    private UUID id;
    private UUID challengeDefinitionId;
    private Integer progress;
    private boolean completed;
    private boolean claimed;
    private Instant expiresAt;
    private Instant completedAt;
    private Instant createdAt;
}
