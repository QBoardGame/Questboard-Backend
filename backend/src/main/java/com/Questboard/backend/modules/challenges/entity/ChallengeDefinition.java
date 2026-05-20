package com.Questboard.backend.modules.challenges.entity;

import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
import com.Questboard.backend.modules.challenges.enums.ChallengeType;
import com.Questboard.backend.modules.challenges.enums.CreatorType;
import com.Questboard.backend.modules.challenges.enums.EventType;
import com.Questboard.backend.modules.challenges.enums.RewardType;
import com.Questboard.backend.modules.challenges.enums.Visibility;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

// @Entity
// @Table(name = "challenge_definition", indexes = {
//         @Index(name = "idx_challenge_game", columnList = "game_id"),
//         @Index(name = "idx_challenge_type", columnList = "challenge_type")
// })
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class ChallengeDefinition {
//     @Id
//     @Column(name = "id", nullable = false)
//     private UUID id;

//     @Column(name = "game_id", nullable = false)
//     private Long gameId;

//     @Column(name = "title", nullable = false)
//     private String title;

//     @Column(name = "description", columnDefinition = "text")
//     private String description;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "challenge_type", nullable = false)
//     private ChallengeType challengeType;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "event_type", nullable = false)
//     private EventType eventType;

//     @Column(name = "target_value", nullable = false)
//     private Long targetValue;

//     @Column(name = "reward_amount", nullable = false)
//     private Long rewardAmount;

//     @Column(name = "rarity_weight")
//     private Integer rarityWeight;

//     @Column(name = "active")
//     private boolean active;

//     @Column(name = "created_at")
//     private Instant createdAt;

//     @Column(name = "updated_at")
//     private Instant updatedAt;

//     @PrePersist
//     protected void onCreate(){
//         this.active = false;
//     }
// }

@Entity
@Table(name = "challenge_definition", indexes = {
        @Index(name = "idx_challenge_game", columnList = "game_id"),
        @Index(name = "idx_challenge_status", columnList = "status"),
        @Index(name = "idx_challenge_schedule", columnList = "starts_at, ends_at"),
        @Index(name = "idx_challenge_creator", columnList = "created_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeDefinition {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    /**
     * Game Reference
     */
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    /**
     * Creator Information
     */
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "creator_type", nullable = false)
    private CreatorType creatorType;

    /**
     * Basic Info
     */
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Challenge Classification
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_type", nullable = false)
    private ChallengeType challengeType;

    /**
     * Trigger Event
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    /**
     * Target Requirement
     */
    @Column(name = "target_value", nullable = false)
    private Long targetValue;

    /**
     * Optional Dynamic Conditions
     * Example:
     * {
     * "mode": "RANKED",
     * "weapon": "SNIPER"
     * }
     */
    @Column(name = "conditions", columnDefinition = "jsonb")
    private String conditions;

    /**
     * Reward Configuration
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    @Column(name = "reward_value", nullable = false)
    private String rewardValue;

    /**
     * Challenge Timing
     */
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    /**
     * Visibility
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    /**
     * Approval / Lifecycle Status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;

    /**
     * Difficulty / Selection Weight
     */
    @Column(name = "rarity_weight")
    private Integer rarityWeight;

    /**
     * Metadata
     */
    @Column(name = "featured")
    private Boolean featured;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (id == null) {
            id = UUID.randomUUID();
        }

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ChallengeStatus.DRAFT;
        }

        if (featured == null) {
            featured = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}
