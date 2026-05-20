// package com.Questboard.backend.modules.challenges.repository;

// public class UserChallengeProgressRepository {

// }

package com.Questboard.backend.modules.challenges.repository;

import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
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

}
