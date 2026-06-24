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

                // long rewardCoins = target * selectedRule.getRewardMultiplier();
                long rewardCoins = challengeType == ChallengeType.DAILY ? 5 : 20;

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

                for (int i = 0; i < 2; i++) {
                        challenges.add(generateChallenge(
                                        gameId,
                                        ChallengeType.DAILY,
                                        ChallengeDifficulty.EASY));
                }

                challenges.add(generateChallenge(
                                gameId,
                                ChallengeType.WEEKLY,
                                ChallengeDifficulty.HARD));

                return challenges;
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