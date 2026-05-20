package com.Questboard.backend.modules.challenges.entity;

import com.Questboard.backend.modules.challenges.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "challenge_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeTemplate {

    @Id
    private UUID id;

    /**
     * Game
     */
    @Column(nullable = false)
    private Long gameId;

    /**
     * Challenge Title
     */
    @Column(nullable = false)
    private String title;

    /**
     * Challenge Description
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * DAILY / WEEKLY / EVENT / CUSTOM
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeType challengeType;

    /**
     * KILL / WIN / DAMAGE / etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    /**
     * Required amount
     * Example:
     * 10 kills
     */
    @Column(nullable = false)
    private Long targetValue;

    /**
     * Reward Type
     * COINS / CASH / ITEM / XP
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType;

    /**
     * Reward Value
     * Example:
     * 100 coins
     * 5 USD
     */
    @Column(nullable = false)
    private String rewardValue;

    /**
     * Difficulty / rarity
     */
    private Integer rarityWeight;

    /**
     * Conditions JSON
     * Example:
     * ranked only
     * weapon = sniper
     */
    @Column(columnDefinition = "TEXT")
    private String conditions;

    /**
     * Template enabled/disabled
     */
    @Column(nullable = false)
    private boolean active;

    /**
     * Creator
     * null = platform system
     * otherwise streamer/brand/admin
     */
    private UUID createdBy;

    /**
     * STREAMER / BRAND / SYSTEM
     */
    @Enumerated(EnumType.STRING)
    private ChallengeCreatorType creatorType;

    /**
     * Creation Time
     */
    private Instant createdAt;

    /**
     * Last Updated
     */
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        createdAt = Instant.now();
        updatedAt = Instant.now();

        if (rarityWeight == null) {
            rarityWeight = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}