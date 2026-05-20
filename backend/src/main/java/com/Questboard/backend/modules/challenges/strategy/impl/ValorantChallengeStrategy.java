// package com.Questboard.backend.modules.challenges.strategy.impl;

// import com.Questboard.backend.modules.challenges.dto.GameEventDto;
// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.entity.GameEvent;
// import com.Questboard.backend.modules.challenges.entity.UserChallenge;
// import com.Questboard.backend.modules.challenges.repository.UserChallengeRepository;
// import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
// import com.Questboard.backend.modules.challenges.service.RewardService;
// import com.Questboard.backend.modules.challenges.strategy.ChallengeStrategy;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.Instant;
// import java.util.List;
// import java.util.UUID;

// // @Component
// // public class ValorantChallengeStrategy implements ChallengeStrategy {
// //     private final Logger log = LoggerFactory.getLogger(ValorantChallengeStrategy.class);
// //     private final UserChallengeRepository userChallengeRepository;
// //     private final ChallengeDefinitionRepository challengeDefinitionRepository;
// //     private final RewardService rewardService;

// //     public ValorantChallengeStrategy(UserChallengeRepository userChallengeRepository,
// //                                     ChallengeDefinitionRepository challengeDefinitionRepository,
// //                                     RewardService rewardService) {
// //         this.userChallengeRepository = userChallengeRepository;
// //         this.challengeDefinitionRepository = challengeDefinitionRepository;
// //         this.rewardService = rewardService;
// //     }

// //     @Override
// //     public boolean supports(Long gameId) {
// //         return gameId != null && gameId == 21640L;
// //     }

// //     @Override
// //     @Transactional
// //     public void process(GameEvent event) {
// //         // Process events for Valorant: update user challenges matching definition event type
// //         List<UserChallenge> list = userChallengeRepository.findByUserIdAndCompletedFalse(event.getUserId());
// //         for (UserChallenge uc : list) {
// //             // In a real system we'd join to definitions; keep it simple: increment progress
// //             // Fetch definition only when needed
// //             if (uc.isCompleted()) continue;
// //             // naive increment
// //             Integer newProgress = uc.getProgress() + (event.getValue() == null ? 1 : event.getValue());
// //             uc.setProgress(newProgress);
// //             // check completion by loading def
// //             challengeDefinitionRepository.findById(uc.getChallengeDefinitionId()).ifPresent(def -> {
// //                 if (newProgress >= def.getTargetValue()) {
// //                     uc.setCompleted(true);
// //                     uc.setCompletedAt(Instant.now());
// //                     rewardService.grantReward(uc.getUserId(), def.getRewardValue(), uc.getId());
// //                 }
// //             });
// //             userChallengeRepository.save(uc);
// //         }
// //     }
// // }

// @Component
// public class ValorantChallengeStrategy
//         implements ChallengeStrategy {

//     @Override
//     public Long supportedGameId() {
//         return 1L;
//     }

//     @Override
//     public List<ChallengeDefinition> getActiveChallenges(
//             Long gameId) {
//         // fetch active valorant challenges
//         return List.of();
//     }

//     @Override
//     public boolean matches(
//             ChallengeDefinition challenge,
//             GameEventDto event) {

//         return challenge.getEventType()
//                 .name()
//                 .equals(event.getEventType().name());
//     }

//     @Override
//     public long extractProgress(
//             GameEventDto event) {
//         return event.getCount();
//     }

// }


package com.Questboard.backend.modules.challenges.strategy.impl;

import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
import com.Questboard.backend.modules.challenges.enums.EventType;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValorantChallengeStrategy implements ChallengeStrategy {

    private static final Long VALORANT_GAME_ID = 21640L;

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
                now
        );
    }

    // -----------------------------
    // EVENT PROCESSING CORE LOGIC
    // -----------------------------

    @Override
    @Transactional
    public void process(GameEventDto event) {

        if (event == null || event.getUserId() == null) {
            return;
        }

        Instant now = Instant.now();

        // 1. Fetch ONLY relevant active challenges (VERY IMPORTANT OPTIMIZATION)
        List<ChallengeDefinition> challenges =
                challengeDefinitionRepository.findActiveByEventType(
                        VALORANT_GAME_ID,
                        event.getEventType(),
                        now
                );

        if (challenges.isEmpty()) {
            return;
        }

        long eventValue = extractProgress(event);

        // 2. Process each matching challenge
        for (ChallengeDefinition challenge : challenges) {

            try {

                // 2.1 Validate custom conditions (future-proof)
                if (!matches(challenge, event)) {
                    continue;
                }

                // 2.2 Get or create user progress (LAZY CREATION)
                UserChallengeProgress progress =
                        progressRepository
                                .findByUserIdAndChallengeId(
                                        event.getUserId(),
                                        challenge.getId()
                                )
                                .orElseGet(() -> createProgress(event.getUserId(), challenge));

                // 2.3 Skip if already completed
                if (progress.isCompleted()) {
                    continue;
                }

                // 2.4 Update progress
                long updatedProgress = progress.getProgress() + eventValue;
                progress.setProgress(updatedProgress);

                // 2.5 Check completion
                if (updatedProgress >= challenge.getTargetValue()) {

                    progress.setCompleted(true);
                    progress.setCompletedAt(Instant.now());

                    // 2.6 Trigger reward ONLY ONCE
                    if (!progress.isClaimed()) {
                        rewardService.grantReward(
                                event.getUserId(),
                                challenge.getRewardType(),
                                challenge.getRewardValue(),
                                challenge.getId()
                        );
                    }

                    log.info("User {} completed challenge {}",
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
    // MATCH LOGIC (EXTENSIBLE)
    // -----------------------------

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

    // -----------------------------
    // EVENT VALUE EXTRACTION
    // -----------------------------

    @Override
    public long extractProgress(GameEventDto event) {

        if (event.getValue() != null) {
            return event.getValue();
        }

        return 21640L;
    }

    // -----------------------------
    // LAZY PROGRESS CREATION
    // -----------------------------

    private UserChallengeProgress createProgress(UUID userId, ChallengeDefinition challenge) {

        UserChallengeProgress progress = UserChallengeProgress.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .challengeId(challenge.getId())
                .progress(0L)
                .targetValue(challenge.getTargetValue())
                .completed(false)
                .claimed(false)
                .build();

        return progressRepository.save(progress);
    }
}