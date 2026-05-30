// package com.Questboard.backend.modules.challenges.generator;

// import java.time.Instant;
// import java.time.temporal.ChronoUnit;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
// import java.util.concurrent.ThreadLocalRandom;

// import org.springframework.stereotype.Service;

// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.entity.ChallengeRule;
// import com.Questboard.backend.modules.challenges.enums.*;
// import com.Questboard.backend.modules.challenges.repository.ChallengeRuleRepository;
// import com.fasterxml.jackson.databind.JsonNode;

// import lombok.RequiredArgsConstructor;
// import tools.jackson.databind.ObjectMapper;
// import tools.jackson.databind.node.ObjectNode;

// // @Service
// // @RequiredArgsConstructor
// // public class ChallengeGeneratorService {

// //         private final ChallengeRuleRepository challengeRuleRepository;

// //         public ChallengeDefinition generateChallenge(
// //                         Long gameId,
// //                         ChallengeType challengeType,
// //                         ChallengeDifficulty difficulty) {

// //                 List<ChallengeRule> rules = challengeRuleRepository
// //                                 .findByGameIdAndChallengeTypeAndDifficultyAndEnabledTrue(
// //                                                 gameId,
// //                                                 challengeType,
// //                                                 difficulty);

// //                 if (rules.isEmpty()) {
// //                         throw new RuntimeException("No challenge rules found");
// //                 }

// //                 ChallengeRule selectedRule = weightedRandom(rules);

// //                 long target = ThreadLocalRandom.current()
// //                                 .nextLong(
// //                                                 selectedRule.getMinTarget(),
// //                                                 selectedRule.getMaxTarget() + 1);

// //                 String title = selectedRule.getTitleTemplate()
// //                                 .replace("{x}", String.valueOf(target));

// //                 // Daily -> smaller reward
// //                 // Weekly -> bigger reward
// //                 long rewardCoins = target * selectedRule.getRewardMultiplier();

// //                 return ChallengeDefinition.builder()
// //                                 .id(UUID.randomUUID())
// //                                 .gameId(gameId)
// //                                 .createdBy(UUID.randomUUID())
// //                                 .creatorType(CreatorType.SYSTEM)
// //                                 .title(title)
// //                                 .description(title)
// //                                 .challengeType(challengeType)
// //                                 .eventType(selectedRule.getEventType())
// //                                 .targetValue(target)
// //                                 .conditions(selectedRule.getConditions())
// //                                 .rewardType(RewardType.COINS)
// //                                 .rewardValue(String.valueOf(rewardCoins))
// //                                 .visibility(Visibility.PUBLIC)
// //                                 .status(ChallengeStatus.ACTIVE)
// //                                 .featured(false)
// //                                 .rarityWeight(selectedRule.getWeight())
// //                                 .startsAt(Instant.now())
// //                                 .endsAt(
// //                                                 challengeType == ChallengeType.DAILY
// //                                                                 ? Instant.now().plus(1, ChronoUnit.DAYS)
// //                                                                 : Instant.now().plus(7, ChronoUnit.DAYS))
// //                                 .build();
// //         }

// //         /**
// //          * Weighted random selection
// //          */
// //         private ChallengeRule weightedRandom(
// //                         List<ChallengeRule> rules) {

// //                 int totalWeight = rules.stream()
// //                                 .mapToInt(ChallengeRule::getWeight)
// //                                 .sum();

// //                 int random = ThreadLocalRandom.current()
// //                                 .nextInt(totalWeight);

// //                 int current = 0;

// //                 for (ChallengeRule rule : rules) {

// //                         current += rule.getWeight();

// //                         if (random < current) {
// //                                 return rule;
// //                         }
// //                 }

// //                 return rules.get(0);
// //         }

// //         public List<ChallengeDefinition> generateChallenges(Long gameId) {

// //                 List<ChallengeDefinition> challenges = new ArrayList<>();

// //                 // 2 easy daily challenges
// //                 challenges.add(
// //                                 generateChallenge(
// //                                                 gameId,
// //                                                 ChallengeType.DAILY,
// //                                                 ChallengeDifficulty.EASY));

// //                 challenges.add(
// //                                 generateChallenge(
// //                                                 gameId,
// //                                                 ChallengeType.DAILY,
// //                                                 ChallengeDifficulty.EASY));

// //                 // 1 hard weekly challenge
// //                 challenges.add(
// //                                 generateChallenge(
// //                                                 gameId,
// //                                                 ChallengeType.WEEKLY,
// //                                                 ChallengeDifficulty.HARD));

// //                 return challenges;
// //         }
// // }

// @Service
// @RequiredArgsConstructor
// public class ChallengeGeneratorService {

//         private final ChallengeRuleRepository challengeRuleRepository;
//         private final ObjectMapper objectMapper;

//         public ChallengeDefinition generateChallenge(
//                         Long gameId,
//                         ChallengeType challengeType,
//                         ChallengeDifficulty difficulty) {

//                 List<ChallengeRule> rules = challengeRuleRepository
//                                 .findByGameIdAndChallengeTypeAndDifficultyAndEnabledTrue(
//                                                 gameId,
//                                                 challengeType,
//                                                 difficulty);

//                 if (rules.isEmpty()) {
//                         throw new RuntimeException("No challenge rules found");
//                 }

//                 ChallengeRule selectedRule = weightedRandom(rules);

//                 long target = ThreadLocalRandom.current()
//                                 .nextLong(
//                                                 selectedRule.getMinTarget(),
//                                                 selectedRule.getMaxTarget() + 1);

//                 String title = selectedRule.getTitleTemplate()
//                                 .replace("{x}", String.valueOf(target));

//                 long rewardCoins = target * selectedRule.getRewardMultiplier();

//                 // -------------------------------------------------
//                 // BUILD CONDITIONS AS JSON (NEW RULE ENGINE FORMAT)
//                 // -------------------------------------------------
//                 JsonNode conditions = selectedRule.getConditions();

//                 // Ensure conditions exist
//                 if (conditions == null || conditions.isEmpty()) {
//                         ObjectNode empty = objectMapper.createObjectNode();
//                         empty.putArray("rules");
//                         conditions = empty;
//                 }

//                 return ChallengeDefinition.builder()
//                                 .id(UUID.randomUUID())
//                                 .gameId(gameId)
//                                 .createdBy(UUID.randomUUID())
//                                 .creatorType(CreatorType.SYSTEM)
//                                 .title(title)
//                                 .description(title)
//                                 .challengeType(challengeType)
//                                 .eventType(selectedRule.getEventType())
//                                 .targetValue(target)
//                                 .conditions(conditions) // 🔥 JSONB direct
//                                 .rewardType(RewardType.COINS)
//                                 .rewardValue(String.valueOf(rewardCoins))
//                                 .visibility(Visibility.PUBLIC)
//                                 .status(ChallengeStatus.ACTIVE)
//                                 .featured(false)
//                                 .rarityWeight(selectedRule.getWeight())
//                                 .startsAt(Instant.now())
//                                 .endsAt(
//                                                 challengeType == ChallengeType.DAILY
//                                                                 ? Instant.now().plus(1, ChronoUnit.DAYS)
//                                                                 : Instant.now().plus(7, ChronoUnit.DAYS))
//                                 .build();
//         }

//         private ChallengeRule weightedRandom(List<ChallengeRule> rules) {

//                 int totalWeight = rules.stream()
//                                 .mapToInt(ChallengeRule::getWeight)
//                                 .sum();

//                 int random = ThreadLocalRandom.current().nextInt(totalWeight);

//                 int current = 0;

//                 for (ChallengeRule rule : rules) {

//                         current += rule.getWeight();

//                         if (random < current) {
//                                 return rule;
//                         }
//                 }

//                 return rules.get(0);
//         }

//         public List<ChallengeDefinition> generateChallenges(Long gameId) {

//                 List<ChallengeDefinition> challenges = new ArrayList<>();

//                 challenges.add(generateChallenge(
//                                 gameId,
//                                 ChallengeType.DAILY,
//                                 ChallengeDifficulty.EASY));

//                 challenges.add(generateChallenge(
//                                 gameId,
//                                 ChallengeType.DAILY,
//                                 ChallengeDifficulty.EASY));

//                 challenges.add(generateChallenge(
//                                 gameId,
//                                 ChallengeType.WEEKLY,
//                                 ChallengeDifficulty.HARD));

//                 return challenges;
//         }
// }

package com.Questboard.backend.modules.challenges.generator;

import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.entity.ChallengeRule;
import com.Questboard.backend.modules.challenges.enums.*;
import com.Questboard.backend.modules.challenges.repository.ChallengeRuleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ChallengeGeneratorService {

        private final ChallengeRuleRepository challengeRuleRepository;
        private final ObjectMapper objectMapper = new ObjectMapper();

        // -------------------------------------------------
        // MAIN GENERATOR
        // -------------------------------------------------
        public ChallengeDefinition generateChallenge(
                        Long gameId,
                        ChallengeType challengeType,
                        ChallengeDifficulty difficulty) {

                List<ChallengeRule> rules = challengeRuleRepository
                                .findByGameIdAndChallengeTypeAndDifficultyAndEnabledTrue(
                                                gameId,
                                                challengeType,
                                                difficulty);

                if (rules.isEmpty()) {
                        throw new RuntimeException("No challenge rules found");
                }

                ChallengeRule selectedRule = weightedRandom(rules);

                long target = ThreadLocalRandom.current()
                                .nextLong(
                                                selectedRule.getMinTarget(),
                                                selectedRule.getMaxTarget() + 1);

                String title = selectedRule.getTitleTemplate()
                                .replace("{x}", String.valueOf(target));

                long rewardCoins = target * selectedRule.getRewardMultiplier();

                // -------------------------------------------------
                // CONDITIONS (JSONB SAFE)
                // -------------------------------------------------
                JsonNode conditions = selectedRule.getConditions();

                if (conditions == null || conditions.isEmpty()) {
                        ObjectNode empty = objectMapper.createObjectNode();
                        empty.putArray("rules");
                        conditions = empty;
                }

                return ChallengeDefinition.builder()
                                .id(UUID.randomUUID())
                                .gameId(gameId)
                                .createdBy(UUID.randomUUID())
                                .creatorType(CreatorType.SYSTEM)
                                .title(title)
                                .description(title)
                                .challengeType(challengeType)
                                .eventType(selectedRule.getEventType())
                                .targetValue(target)
                                .conditions(conditions)
                                .rewardType(RewardType.COINS)
                                .rewardValue(String.valueOf(rewardCoins))
                                .visibility(Visibility.PUBLIC)
                                .status(ChallengeStatus.ACTIVE)
                                .featured(false)
                                .totalPlayersAllowed(null)
                                .rarityWeight(selectedRule.getWeight())
                                .startsAt(Instant.now())
                                .endsAt(
                                                challengeType == ChallengeType.DAILY
                                                                ? Instant.now().plus(1, ChronoUnit.DAYS)
                                                                : Instant.now().plus(7, ChronoUnit.DAYS))
                                .build();
        }

        // -------------------------------------------------
        // MULTIPLE CHALLENGES GENERATION
        // -------------------------------------------------
        public List<ChallengeDefinition> generateChallenges(Long gameId) {

                List<ChallengeDefinition> challenges = new ArrayList<>();

                // -----------------------------
                // DAILY CHALLENGES (LOW REWARD)
                // -----------------------------
                for (int i = 0; i < 2; i++) {

                        ChallengeDefinition daily = generateChallenge(
                                        gameId,
                                        ChallengeType.DAILY,
                                        ChallengeDifficulty.EASY);

                        daily = applyRewardMultiplier(daily, 1.0);

                        challenges.add(daily);
                }

                // -----------------------------
                // WEEKLY CHALLENGE (HIGHER REWARD)
                // -----------------------------
                ChallengeDefinition weekly = generateChallenge(
                                gameId,
                                ChallengeType.WEEKLY,
                                ChallengeDifficulty.HARD);

                weekly = applyRewardMultiplier(weekly, 2.5);

                challenges.add(weekly);

                return challenges;
        }

        // -------------------------------------------------
        // REWARD SCALING ENGINE
        // -------------------------------------------------
        private ChallengeDefinition applyRewardMultiplier(
                        ChallengeDefinition challenge,
                        double multiplier) {

                long base = Long.parseLong(challenge.getRewardValue());
                long scaled = (long) (base * multiplier);

                challenge.setRewardValue(String.valueOf(scaled));

                return challenge;
        }

        // -------------------------------------------------
        // WEIGHTED RANDOM SELECTION
        // -------------------------------------------------
        private ChallengeRule weightedRandom(List<ChallengeRule> rules) {

                int totalWeight = rules.stream()
                                .mapToInt(ChallengeRule::getWeight)
                                .sum();

                int random = ThreadLocalRandom.current().nextInt(totalWeight);

                int current = 0;

                for (ChallengeRule rule : rules) {
                        current += rule.getWeight();
                        if (random < current) {
                                return rule;
                        }
                }

                return rules.get(0);
        }
}