// package com.Questboard.backend.modules.challenges.strategy.impl;

// import com.Questboard.backend.modules.challenges.entity.GameEvent;
// import com.Questboard.backend.modules.challenges.entity.UserChallenge;
// import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
// import com.Questboard.backend.modules.challenges.repository.UserChallengeRepository;
// import com.Questboard.backend.modules.challenges.service.RewardService;
// import com.Questboard.backend.modules.challenges.strategy.ChallengeStrategy;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.Instant;
// import java.util.List;

// @Component
// public class HearthstoneChallengeStrategy implements ChallengeStrategy {
//     private final Logger log = LoggerFactory.getLogger(HearthstoneChallengeStrategy.class);
//     private final UserChallengeRepository userChallengeRepository;
//     private final ChallengeDefinitionRepository challengeDefinitionRepository;
//     private final RewardService rewardService;

//     public HearthstoneChallengeStrategy(UserChallengeRepository userChallengeRepository,
//                                         ChallengeDefinitionRepository challengeDefinitionRepository,
//                                         RewardService rewardService) {
//         this.userChallengeRepository = userChallengeRepository;
//         this.challengeDefinitionRepository = challengeDefinitionRepository;
//         this.rewardService = rewardService;
//     }

//     @Override
//     public boolean supports(Long gameId) {
//         return gameId != null && gameId == 2L;
//     }

//     @Override
//     @Transactional
//     public void process(GameEvent event) {
//         List<UserChallenge> list = userChallengeRepository.findByUserIdAndCompletedFalse(event.getUserId());
//         for (UserChallenge uc : list) {
//             if (uc.isCompleted()) continue;
//             Integer newProgress = uc.getProgress() + (event.getValue() == null ? 1 : event.getValue());
//             uc.setProgress(newProgress);
//             challengeDefinitionRepository.findById(uc.getChallengeDefinitionId()).ifPresent(def -> {
//                 if (newProgress >= def.getTargetValue()) {
//                     uc.setCompleted(true);
//                     uc.setCompletedAt(Instant.now());
//                     rewardService.grantReward(uc.getUserId(), def.getRewardValue(), uc.getId());
//                 }
//             });
//             userChallengeRepository.save(uc);
//         }
//     }
// }

package com.Questboard.backend.modules.challenges.strategy.impl;

import com.Questboard.backend.modules.challenges.entity.GameEvent;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.enums.RewardType;
import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
import com.Questboard.backend.modules.challenges.repository.UserChallengeProgressRepository;
import com.Questboard.backend.modules.challenges.service.RewardService;
import com.Questboard.backend.modules.challenges.strategy.ChallengeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HearthstoneChallengeStrategy implements ChallengeStrategy {

    private static final Long HEARTHSTONE_GAME_ID = 9898L;

    private final UserChallengeProgressRepository progressRepository;
    private final ChallengeDefinitionRepository challengeDefinitionRepository;
    private final RewardService rewardService;

    // -----------------------------
    // GAME SUPPORT
    // -----------------------------
    @Override
    public Long supportedGameId() {
        return HEARTHSTONE_GAME_ID;
    }

    // -----------------------------
    // ACTIVE CHALLENGES
    // -----------------------------
    @Override
    public List<ChallengeDefinition> getActiveChallenges(Long gameId) {
        Instant now = Instant.now();

        return challengeDefinitionRepository.findActiveChallengesByGameId(
                gameId,
                now);
    }

    // -----------------------------
    // MAIN EVENT PROCESSING
    // -----------------------------
    @Override
    @Transactional
    public void process(GameEventDto event) {

        if (event == null || event.getUserId() == null) {
            return;
        }

        long eventValue = extractValue(event);

        // 1. Fetch only relevant active challenges
        List<ChallengeDefinition> challenges = challengeDefinitionRepository.findActiveByEventType(
                HEARTHSTONE_GAME_ID,
                event.getEventType(),
                Instant.now());

        if (challenges.isEmpty()) {
            return;
        }

        // 2. Process each challenge
        for (ChallengeDefinition challenge : challenges) {

            try {

                // 2.1 Get or create progress
                UserChallengeProgress progress = progressRepository
                        .findByUserIdAndChallengeId(
                                event.getUserId(),
                                challenge.getId())
                        .orElseGet(() -> createProgress(event, challenge));

                if (progress.isCompleted()) {
                    continue;
                }

                // 2.2 Update progress
                long updated = progress.getProgress() + eventValue;
                progress.setProgress(updated);

                // 2.3 Completion check
                if (updated >= challenge.getTargetValue()) {

                    progress.setCompleted(true);
                    progress.setCompletedAt(Instant.now());

                    // 2.4 Reward (ONLY ONCE)
                    if (!progress.isClaimed()) {
                        rewardService.grantReward(
                                event.getUserId(),
                                RewardType.valueOf(challenge.getRewardType().name()),
                                String.valueOf(challenge.getRewardValue()),
                                challenge.getId());
                    }

                    log.info("User {} completed Hearthstone challenge {}",
                            event.getUserId(),
                            challenge.getId());
                }

                progressRepository.save(progress);

            } catch (Exception e) {
                log.error("Error processing challenge {} for user {}",
                        challenge.getId(),
                        event.getUserId(),
                        e);
            }
        }
    }

    // -----------------------------
    // VALUE EXTRACTION
    // -----------------------------
    private long extractValue(GameEventDto event) {
        return event.getValue() != null ? event.getValue() : 1L;
    }

    // -----------------------------
    // PROGRESS CREATION
    // -----------------------------
    private UserChallengeProgress createProgress(GameEventDto event,
            ChallengeDefinition challenge) {

        return progressRepository.save(
                UserChallengeProgress.builder()
                        .id(java.util.UUID.randomUUID())
                        .userId(event.getUserId())
                        .challengeId(challenge.getId())
                        .progress(0L)
                        .targetValue(challenge.getTargetValue())
                        .completed(false)
                        .claimed(false)
                        .build());
    }

    @Override
    public long extractProgress(GameEventDto event) {

        if (event.getValue() != null) {
            return event.getValue();
        }

        return 2L;
    }

    @Override
    public boolean matches(ChallengeDefinition challenge, GameEventDto event) {

        // 1. Basic event type match
        if (challenge.getEventType() != event.getEventType()) {
            return false;
        }

        // 2. Future: JSON conditions support
        // Example: ranked mode, weapon type, map, etc.
        if (challenge.getConditions() != null) {

            // You can extend later with Jackson parsing
            // JsonNode conditions = objectMapper.readTree(...)
            // return evaluateConditions(...)
        }

        return true;
    }
}