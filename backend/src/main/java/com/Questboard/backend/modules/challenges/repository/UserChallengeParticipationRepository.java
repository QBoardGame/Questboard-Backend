package com.Questboard.backend.modules.challenges.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Questboard.backend.modules.challenges.entity.ParticipationStatus;
import com.Questboard.backend.modules.challenges.entity.UserChallengeParticipation;

public interface UserChallengeParticipationRepository
        extends JpaRepository<UserChallengeParticipation, UUID> {

    long countByChallengeIdAndStatus(UUID challengeId, ParticipationStatus status);

    boolean existsByUserIdAndChallengeIdAndStatus(
        UUID userId,
        UUID challengeId,
        ParticipationStatus status
    );
}
