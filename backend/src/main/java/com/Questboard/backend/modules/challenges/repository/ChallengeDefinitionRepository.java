// package com.Questboard.backend.modules.challenges.repository;

// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.enums.ChallengeType;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.UUID;

// @Repository
// public interface ChallengeDefinitionRepository extends JpaRepository<ChallengeDefinition, UUID> {
//     List<ChallengeDefinition> findByGameIdAndChallengeTypeAndActive(Long gameId, ChallengeType challengeType, boolean active);
//     List<ChallengeDefinition> findByGameIdAndActive(Long gameId, boolean active);
// }

package com.Questboard.backend.modules.challenges.repository;

import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
import com.Questboard.backend.modules.challenges.enums.ChallengeType;
import com.Questboard.backend.modules.challenges.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChallengeDefinitionRepository
        extends JpaRepository<ChallengeDefinition, UUID> {

    /**
     * Get all active challenges for a game in time window
     */
    @Query("""
                SELECT c FROM ChallengeDefinition c
                WHERE c.gameId = :gameId
                AND c.status = :status
                AND c.startsAt <= :now
                AND c.endsAt >= :now
            """)
    List<ChallengeDefinition> findActiveChallenges(
            Long gameId,
            ChallengeStatus status,
            Instant now);

    /**
     * Filter by event type (VERY IMPORTANT for event engine)
     */
    @Query("""
                SELECT c FROM ChallengeDefinition c
                WHERE c.gameId = :gameId
                AND c.eventType = :eventType
                AND c.status = 'ACTIVE'
                AND c.startsAt <= :now
                AND c.endsAt >= :now
            """)
    List<ChallengeDefinition> findActiveByEventType(
            Long gameId,
            EventType eventType,
            Instant now);

    /**
     * Get all challenges created by streamer/brand
     */
    List<ChallengeDefinition> findByCreatedBy(UUID createdBy);

    /**
     * Get all scheduled (upcoming) challenges
     */
    @Query("""
                SELECT c FROM ChallengeDefinition c
                WHERE c.status = 'SCHEDULED'
                AND c.startsAt > :now
            """)
    List<ChallengeDefinition> findUpcomingChallenges(Instant now);

    /**
     * Get challenges for game by type (DAILY / WEEKLY / EVENT)
     */
    List<ChallengeDefinition> findByGameIdAndChallengeTypeAndStatus(
            Long gameId,
            ChallengeType type,
            ChallengeStatus status);

    List<ChallengeDefinition> findActiveChallengesByGameId(Long gameId, Instant now);

    List<ChallengeDefinition> findByGameIdAndChallengeType(Long gameId, ChallengeType type);

    boolean existsByGameIdAndChallengeTypeAndStartsAtBetween(
            Long gameId,
            ChallengeType challengeType,
            Instant start,
            Instant end);

    @Modifying
    @Transactional
    @Query("""
                UPDATE ChallengeDefinition c
                SET c.status = :inactiveStatus
                WHERE c.endsAt < :now
                AND c.status = :activeStatus
            """)
    int deactivateExpiredChallenges(
            @Param("now") Instant now,
            @Param("activeStatus") ChallengeStatus activeStatus,
            @Param("inactiveStatus") ChallengeStatus inactiveStatus);
}
