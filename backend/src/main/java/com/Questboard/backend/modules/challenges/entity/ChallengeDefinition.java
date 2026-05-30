package com.Questboard.backend.modules.challenges.entity;

import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
import com.Questboard.backend.modules.challenges.enums.ChallengeType;
import com.Questboard.backend.modules.challenges.enums.CreatorType;
import com.Questboard.backend.modules.challenges.enums.EventType;
import com.Questboard.backend.modules.challenges.enums.RewardType;
import com.Questboard.backend.modules.challenges.enums.Visibility;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


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
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conditions", columnDefinition = "jsonb")
    private JsonNode conditions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

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

    @Column(name = "total_player_allowed")
    private Long totalPlayersAllowed;

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
