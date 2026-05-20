package com.Questboard.backend.modules.challenges.entity;

import java.time.Instant;
import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_challenge_progress", indexes = {
        @Index(name = "idx_ucp_user", columnList = "user_id"),
        @Index(name = "idx_ucp_challenge", columnList = "challenge_id"),
        @Index(name = "idx_ucp_completed", columnList = "completed")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChallengeProgress {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    /**
     * Current progress
     */
    @Column(nullable = false)
    private Long progress;

    /**
     * Target copied from challenge
     * Useful for snapshots/history
     */
    @Column(name = "target_value", nullable = false)
    private Long targetValue;

    /**
     * Completion state
     */
    @Column(nullable = false)
    private boolean completed;

    /**
     * Reward claimed
     */
    @Column(nullable = false)
    private boolean claimed;

    /**
     * Completion timestamp
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Last event update
     */
    @Column(name = "updated_at")
    private Instant updatedAt;

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
    }

}
