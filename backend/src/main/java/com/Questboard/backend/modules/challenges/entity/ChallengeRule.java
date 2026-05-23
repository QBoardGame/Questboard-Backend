package com.Questboard.backend.modules.challenges.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.Questboard.backend.modules.challenges.enums.ChallengeDifficulty;
import com.Questboard.backend.modules.challenges.enums.ChallengeType;
import com.Questboard.backend.modules.challenges.enums.EventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "challenge_rule")
@Getter
@Setter
public class ChallengeRule {

    @Id
    private UUID id;

    private Long gameId;

    private String titleTemplate;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private ChallengeType challengeType;

    /**
     * min/max random target
     */
    private Long minTarget;

    private Long maxTarget;

    /**
     * Reward scaling
     */
    private Integer rewardMultiplier;

    /**
     * Weight for random selection
     */
    private Integer weight;

    /**
     * Optional conditions
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String conditions;

    @Enumerated(EnumType.STRING)
    private ChallengeDifficulty difficulty;

    private Boolean enabled;
}