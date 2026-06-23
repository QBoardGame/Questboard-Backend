package com.Questboard.backend.modules.challenges.repository;

import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.enums.EventType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserChallengeProgressRepository
                extends JpaRepository<UserChallengeProgress, UUID> {

        /**
         * Find user progress for a specific challenge
         */
        Optional<UserChallengeProgress> findByUserIdAndChallengeId(
                        UUID userId,
                        UUID challengeId);

        /**
         * Get all progress for a user (profile screen)
         */
        List<UserChallengeProgress> findByUserId(UUID userId);

        /**
         * Get completed but unclaimed rewards
         */
        @Query("""
                            SELECT u FROM UserChallengeProgress u
                            WHERE u.userId = :userId
                            AND u.completed = true
                            AND u.claimed = false
                        """)
        List<UserChallengeProgress> findUnclaimedRewards(UUID userId);

        /**
         * Bulk increment progress (useful for optimization)
         */
        @Modifying
        @Query("""
                            UPDATE UserChallengeProgress u
                            SET u.progress = u.progress + :amount
                            WHERE u.userId = :userId
                            AND u.challengeId = :challengeId
                        """)
        void incrementProgress(
                        UUID userId,
                        UUID challengeId,
                        long amount);

        /**
         * Get all active progress for leaderboard or analytics
         */
        @Query("""
                            SELECT u FROM UserChallengeProgress u
                            WHERE u.completed = false
                        """)
        List<UserChallengeProgress> findActiveProgress();

        List<UserChallengeProgress> findByUserIdAndEventTypeAndCompletedFalse(
                        UUID userId,
                        EventType eventType);

        List<UserChallengeProgress> findByUserIdAndGameIdAndCompletedFalse(
                        UUID userId,
                        Long gameId);

        boolean existsByUserIdAndChallengeId(
                        UUID userId,
                        UUID challengeId);

        UserChallengeProgress findByUserIdAndChallengeIdAndGameIdAndCompleted(UUID userId, UUID challengeId,
                        Long gameId,
                        boolean isCompleted);

        @Query("""
                        UPDATE UserChallengeProgress u
                        SET u.rewardProcessing = true
                        WHERE u.id = :id
                        AND u.completed = true
                        AND u.claimed = false
                        AND u.rewardProcessing = false
                        AND (u.expiresAt IS NULL OR u.expiresAt > CURRENT_TIMESTAMP)
                        """)
        int lockForRewardProcessing(UUID id);

        @Modifying
        @Query("""
                        UPDATE UserChallengeProgress p
                        SET p.rewardProcessing = true
                        WHERE p.id = :id
                        AND p.rewardProcessing = false
                        AND p.completed = true
                        AND p.claimed = false
                        """)
        int lockForReward(UUID id);

        @Modifying
        @Query("""
                        UPDATE UserChallengeProgress p
                        SET p.claimed = true
                        WHERE p.id = :id
                        AND p.claimed = false
                        """)
        int markClaimed(UUID id);

}
