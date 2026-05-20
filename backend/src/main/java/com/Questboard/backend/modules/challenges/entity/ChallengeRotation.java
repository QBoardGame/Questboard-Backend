package com.Questboard.backend.modules.challenges.entity;

import java.time.Instant;
import java.util.UUID;

import com.Questboard.backend.modules.challenges.enums.ChallengeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "challenge_rotation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeRotation {

    @Id
    private UUID id;

    private Long gameId;

    private UUID templateId;

    @Enumerated(EnumType.STRING)
    private ChallengeType type; // DAILY / WEEKLY / MONTHLY

    private Instant startAt;

    private Instant endAt;

    private boolean active;

    private Instant createdAt;
}
