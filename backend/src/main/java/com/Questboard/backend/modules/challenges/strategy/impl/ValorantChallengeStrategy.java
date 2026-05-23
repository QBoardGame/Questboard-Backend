package com.Questboard.backend.modules.challenges.strategy.impl;

import com.Questboard.backend.modules.challenges.dto.CurrentProgressDto;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
import com.Questboard.backend.modules.challenges.repository.UserChallengeProgressRepository;
import com.Questboard.backend.modules.challenges.service.RewardService;
import com.Questboard.backend.modules.challenges.strategy.ChallengeStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValorantChallengeStrategy implements ChallengeStrategy {

    private static final Long VALORANT_GAME_ID = 21640L;
    private final ObjectMapper objectMapper  = new ObjectMapper();;

    private final ChallengeDefinitionRepository challengeDefinitionRepository;
    private final UserChallengeProgressRepository progressRepository;
    private final RewardService rewardService;

    // -----------------------------
    // BASIC CONFIG
    // -----------------------------

    @Override
    public Long supportedGameId() {
        return VALORANT_GAME_ID;
    }

    // -----------------------------
    // FETCH ACTIVE CHALLENGES
    // -----------------------------

    @Override
    public List<ChallengeDefinition> getActiveChallenges(Long gameId) {
        Instant now = Instant.now();

        return challengeDefinitionRepository.findActiveChallenges(
                gameId,
                ChallengeStatus.ACTIVE,
                now);
    }

    @Override
    public boolean matches(ChallengeDefinition challenge, GameEventDto event) {

        // -------------------------------------------------
        // 1. STRICT EVENT TYPE CHECK (MANDATORY)
        // -------------------------------------------------
        if (challenge.getEventType() != event.getEventType()) {
            return false;
        }

        // -------------------------------------------------
        // 2. NO CONDITIONS → BASIC MATCH ONLY
        // -------------------------------------------------
        if (challenge.getConditions() == null ||
                challenge.getConditions().isBlank()) {
            return true;
        }

        try {

            // -------------------------------------------------
            // 3. PARSE CONDITIONS JSON
            // -------------------------------------------------
            JsonNode conditions = objectMapper.readTree(challenge.getConditions());

            // -------------------------------------------------
            // 4. MODE CHECK (ranked / casual)
            // -------------------------------------------------
            // JsonNode modeNode = conditions.get("mode");
            // if (modeNode != null &&
            // event.getMode() != null &&
            // !modeNode.asText().equalsIgnoreCase(event.getMode())) {
            // return false;
            // }

            // // -------------------------------------------------
            // // 5. MAP CHECK
            // // -------------------------------------------------
            // JsonNode mapNode = conditions.get("map");
            // if (mapNode != null &&
            // event.getMap() != null &&
            // !mapNode.asText().equalsIgnoreCase(event.getMap())) {
            // return false;
            // }

            // // -------------------------------------------------
            // // 6. WEAPON CHECK
            // // -------------------------------------------------
            // JsonNode weaponNode = conditions.get("weapon");
            // if (weaponNode != null &&
            // event.getWeapon() != null &&
            // !weaponNode.asText().equalsIgnoreCase(event.getWeapon())) {
            // return false;
            // }

            // -------------------------------------------------
            // 7. MIN VALUE CHECK (optional threshold)
            // -------------------------------------------------
            JsonNode minValueNode = conditions.get("minValue");
            if (minValueNode != null &&
                    event.getValue() != null &&
                    event.getValue() < minValueNode.asLong()) {
                return false;
            }

            // -------------------------------------------------
            // 8. ALL CONDITIONS PASSED
            // -------------------------------------------------
            return true;

        } catch (Exception e) {

            // -------------------------------------------------
            // SAFE FALLBACK: if JSON is broken, DO NOT MATCH
            // -------------------------------------------------
            log.error("Invalid challenge conditions for challenge {}",
                    challenge.getId(), e);

            return false;
        }
    }

    @Override
    @Transactional
    public void process(GameEventDto event, UUID userId) {

        if (event == null) {
            return;
        }

        Instant now = Instant.now();

        // ---------------------------------------
        // 1. FETCH ONLY ACTIVE + RELEVANT CHALLENGES
        // ---------------------------------------
        List<ChallengeDefinition> challenges = challengeDefinitionRepository.findActiveByEventType(
                VALORANT_GAME_ID,
                event.getEventType(),
                now);

        if (challenges.isEmpty()) {
            return;
        }

        long eventValue = event.getValue() != null ? event.getValue() : 0L;

        // ---------------------------------------
        // 2. PROCESS EACH CHALLENGE
        // ---------------------------------------
        for (ChallengeDefinition challenge : challenges) {

            try {

                // ---------------------------------------
                // 2.1 STRICT MATCHING (conditions)
                // ---------------------------------------
                if (!matches(challenge, event)) {
                    continue;
                }

                // ---------------------------------------
                // 2.2 EXPIRY CHECK (defensive)
                // ---------------------------------------
                if (challenge.getEndsAt() != null &&
                        now.isAfter(challenge.getEndsAt())) {
                    continue;
                }

                // ---------------------------------------
                // 2.3 FIND OR CREATE PROGRESS (SAFE)
                // ---------------------------------------
                UserChallengeProgress progress = progressRepository
                        .findByUserIdAndChallengeId(
                                userId,
                                challenge.getId())
                        .orElseGet(() -> createAndSaveProgress(
                                userId,
                                challenge));

                // ---------------------------------------
                // 2.4 SKIP IF ALREADY COMPLETED
                // ---------------------------------------
                if (progress.isCompleted()) {
                    continue;
                }

                // ---------------------------------------
                // 2.5 SKIP IF EXPIRED PROGRESS
                // ---------------------------------------
                if (progress.getExpiresAt() != null &&
                        now.isAfter(progress.getExpiresAt())) {
                    continue;
                }

                // ---------------------------------------
                // 2.6 UPDATE PROGRESS (CORE LOGIC)
                // ---------------------------------------
                long updatedProgress = progress.getProgress() + eventValue;
                progress.setProgress(updatedProgress);

                // ---------------------------------------
                // 2.7 COMPLETION CHECK
                // ---------------------------------------
                if (updatedProgress >= challenge.getTargetValue()) {

                    progress.setCompleted(true);
                    progress.setCompletedAt(now);

                    // ---------------------------------------
                    // 2.8 REWARD (IDEMPOTENT SAFETY)
                    // ---------------------------------------
                    if (!progress.isClaimed()) {

                        rewardService.grantReward(
                                userId,
                                challenge.getRewardType(),
                                challenge.getRewardValue(),
                                challenge.getId());

                        progress.setClaimed(true);
                    }

                    log.info("User {} completed challenge {}",
                            userId,
                            challenge.getId());
                }

                // ---------------------------------------
                // 2.9 SAVE PROGRESS
                // ---------------------------------------
                progressRepository.save(progress);

            } catch (Exception e) {

                log.error(
                        "Error processing challenge {} for user {}",
                        challenge.getId(),
                        userId,
                        e);
            }
        }
    }

    // -----------------------------
    // EVENT VALUE EXTRACTION
    // -----------------------------

    @Override
    public UserChallengeProgress extractProgress(CurrentProgressDto request, UUID userId) {

        // return event.getValue() != null ? event.getValue() : 0L;
        UUID challengeId = request.getChallengeId();
        Long gameId = request.getGameId();
        return progressRepository.findByUserIdAndChallengeIdAndGameIdAndCompleted(userId, challengeId, gameId, false);
    }

    // -----------------------------
    // LAZY PROGRESS CREATION
    // -----------------------------

    private UserChallengeProgress createAndSaveProgress(
            UUID userId,
            ChallengeDefinition challenge) {

        UserChallengeProgress progress = UserChallengeProgress.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .challengeId(challenge.getId())
                .eventType(challenge.getEventType())
                .progress(0L)
                .gameId(VALORANT_GAME_ID)
                .targetValue(challenge.getTargetValue())
                .completed(false)
                .claimed(false)
                .startedAt(Instant.now())
                .expiresAt(challenge.getEndsAt())
                .build();

        return progressRepository.save(progress);
    }
}