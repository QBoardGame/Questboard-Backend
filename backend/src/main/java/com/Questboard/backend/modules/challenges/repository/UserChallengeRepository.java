package com.Questboard.backend.modules.challenges.repository;

import com.Questboard.backend.modules.challenges.entity.UserChallenge;
import com.Questboard.backend.modules.challenges.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, UUID> {
    Page<UserChallenge> findByUserIdAndCompleted(UUID userId, boolean completed, Pageable pageable);
    List<UserChallenge> findByUserIdAndCompletedFalse(UUID userId);
    List<UserChallenge> findByUserIdAndChallengeDefinitionIdIn(UUID userId, List<UUID> defIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select uc from UserChallenge uc where uc.userId = :userId and uc.gameId = :gameId and uc.eventType = :eventType and uc.completed = false and (uc.expiresAt is null or uc.expiresAt > :now)")
    List<UserChallenge> findActiveChallengesForUpdate(@Param("userId") UUID userId,
                                                      @Param("gameId") Long gameId,
                                                      @Param("eventType") EventType eventType,
                                                      @Param("now") Instant now);
}
