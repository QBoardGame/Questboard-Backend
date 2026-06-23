package com.Questboard.backend.modules.challenges.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

import com.Questboard.backend.modules.challenges.enums.ChallengeType;
import com.Questboard.backend.modules.challenges.enums.EventType;
import com.Questboard.backend.modules.challenges.enums.RewardType;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallengeProgressDto {

    private UUID id;

    private UUID userId;

    private UUID challengeId;
    private ChallengeType challengeType;

    private String title;
    private String description;

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
    private RewardType rewardType;
    private String rewardValue;
    private EventType eventType;

    /**
     * Completion Time
     */
    private Instant completedAt;

    /**
     * Last Updated
     */
    private Instant updatedAt;
    private Instant endsAt;

}
