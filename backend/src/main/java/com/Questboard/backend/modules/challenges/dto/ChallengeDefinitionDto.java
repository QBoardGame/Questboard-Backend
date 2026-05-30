// package com.Questboard.backend.modules.challenges.dto;

// import com.Questboard.backend.modules.challenges.enums.ChallengeType;
// import com.Questboard.backend.modules.challenges.enums.EventType;
// import lombok.*;

// import java.time.Instant;
// import java.util.UUID;

// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class ChallengeDefinitionDto {
//     private UUID id;
//     private Long gameId;
//     private String title;
//     private String description;
//     private ChallengeType challengeType;
//     private EventType eventType;
//     private Long targetValue;
//     private Long rewardAmount;
//     private Integer rarityWeight;
//     private boolean active;
//     private Instant createdAt;
//     private Instant updatedAt;
// }

package com.Questboard.backend.modules.challenges.dto;

import com.Questboard.backend.modules.challenges.enums.*;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeDefinitionDto {

    /**
     * Challenge ID
     */
    private UUID id;

    /**
     * Game Reference
     */
    private Long gameId;

    /**
     * Creator Information
     */
    private UUID createdBy;

    private CreatorType creatorType;

    /**
     * Basic Information
     */
    private String title;

    private String description;

    /**
     * Challenge Classification
     */
    private ChallengeType challengeType;

    private EventType eventType;

    /**
     * Progress Target
     */
    private Long targetValue;

    /**
     * Optional Conditions JSON
     *
     * Example:
     * {
     * "mode": "RANKED",
     * "weapon": "SNIPER"
     * }
     */
    private JsonNode conditions;

    /**
     * Reward Information
     */
    private RewardType rewardType;

    private String rewardValue;

    /**
     * Scheduling
     */
    private Instant startsAt;

    private Instant endsAt;

    /**
     * Visibility
     */
    private Visibility visibility;

    /**
     * Lifecycle Status
     */
    private ChallengeStatus status;

    /**
     * Difficulty / Weight
     */
    private Integer rarityWeight;

    /**
     * UI Metadata
     */
    private Boolean featured;

    /**
     * Audit Metadata
     */
    private Instant createdAt;

    private Instant updatedAt;

}
