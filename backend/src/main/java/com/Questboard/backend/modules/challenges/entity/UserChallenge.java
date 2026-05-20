package com.Questboard.backend.modules.challenges.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

import com.Questboard.backend.modules.challenges.enums.EventType;

@Entity
@Table(name = "user_challenge", indexes = {
        @Index(name = "idx_user_challenge_user", columnList = "user_id"),
        @Index(name = "idx_user_challenge_def", columnList = "challenge_definition_id"),
        @Index(name = "idx_user_game_event_completed", columnList = "user_id, game_id, event_type, completed"),
        @Index(name = "idx_user_completed", columnList = "user_id, completed")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallenge {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "challenge_definition_id", nullable = false)
    private UUID challengeDefinitionId;

    // Snapshot fields to avoid joins during progress updates
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "progress", nullable = false)
    private Integer progress;

    @Column(name = "target_value")
    private Integer targetValue;

    @Column(name = "reward_amount")
    private Integer rewardAmount;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "claimed", nullable = false)
    private boolean claimed;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at")
    private Instant createdAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.completed = false;
        this.claimed = false;
        this.progress = 0;
    }
}
