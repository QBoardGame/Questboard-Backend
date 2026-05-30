package com.Questboard.backend.modules.challenges.entity;

import java.time.Instant;
import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_challenge_participation",
       indexes = {
           @Index(name = "idx_part_challenge", columnList = "challenge_id"),
           @Index(name = "idx_part_user", columnList = "user_id")
       })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserChallengeParticipation {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    private Instant joinedAt;

    private Instant leftAt;
}
