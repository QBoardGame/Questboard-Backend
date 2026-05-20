package com.Questboard.backend.modules.challenges.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallengeProgressDto {

    private UUID id;

    private UUID userId;

    private UUID challengeId;

    /**
     * Current Progress
     */
    private Long progress;

    /**
     * Snapshot of target
     */
    private Long targetValue;

    /**
     * Completion State
     */
    private boolean completed;

    /**
     * Reward Claimed
     */
    private boolean claimed;

    /**
     * Completion Time
     */
    private Instant completedAt;

    /**
     * Last Updated
     */
    private Instant updatedAt;

}
