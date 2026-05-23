package com.Questboard.backend.modules.challenges.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Questboard.backend.modules.challenges.entity.ChallengeRule;
import com.Questboard.backend.modules.challenges.enums.ChallengeDifficulty;
import com.Questboard.backend.modules.challenges.enums.ChallengeType;

public interface ChallengeRuleRepository extends JpaRepository<ChallengeRule, UUID> {
    List<ChallengeRule> findByGameIdAndChallengeTypeAndEnabledTrue(
            Long gameId,
            ChallengeType challengeType);

    List<ChallengeRule> findByGameIdAndChallengeTypeAndDifficultyAndEnabledTrue(
            Long gameId,
            ChallengeType challengeType,
            ChallengeDifficulty difficulty);
}
