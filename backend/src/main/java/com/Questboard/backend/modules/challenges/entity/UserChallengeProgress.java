package com.Questboard.backend.modules.challenges.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.Questboard.backend.modules.challenges.enums.ChallengeType;
import com.Questboard.backend.modules.challenges.enums.EventType;
import com.Questboard.backend.modules.challenges.enums.RewardType;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_challenge_progress", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_challenge", columnNames = { "user_id", "challenge_id" })
}, indexes = {
        @Index(name = "idx_ucp_user", columnList = "user_id"),
        @Index(name = "idx_ucp_status", columnList = "completed"),
        @Index(name = "idx_ucp_event", columnList = "event_type")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeProgress {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private Long progress;

    @Column(name = "target_value", nullable = false)
    private Long targetValue;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "reward_processing", nullable = false)
    private boolean rewardProcessing;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conditions", columnDefinition = "jsonb")
    private JsonNode conditions;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_type", nullable = false)
    private ChallengeType challengeType;

    @Column(nullable = false)
    private boolean claimed;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "reward_value", nullable = false)
    private String rewardValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    @Version
    private Long version;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (progress == null) {
            progress = 0L;
        }

        completed = false;
        claimed = false;
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
        updateCompletionStatus();
    }

    private void updateCompletionStatus() {
        if (progress != null && targetValue != null && progress >= targetValue && !isExpired()) {
            completed = true;

            if (completedAt == null) {
                completedAt = Instant.now();
            }
        } else {
            completed = false;
            completedAt = null;
        }

    }

    private boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
}