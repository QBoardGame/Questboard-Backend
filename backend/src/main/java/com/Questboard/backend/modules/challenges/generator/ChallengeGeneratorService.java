package com.Questboard.backend.modules.challenges.generator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.entity.ChallengeRule;
import com.Questboard.backend.modules.challenges.enums.*;
import com.Questboard.backend.modules.challenges.repository.ChallengeRuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeGeneratorService {

        private final ChallengeRuleRepository challengeRuleRepository;

        // public ChallengeDefinition generateChallenge(
        // Long gameId,
        // ChallengeType challengeType) {

        // List<ChallengeRule> rules = challengeRuleRepository
        // .findByGameIdAndChallengeTypeAndEnabledTrue(
        // gameId,
        // challengeType);

        // if (rules.isEmpty()) {
        // throw new RuntimeException(
        // "No challenge rules found");
        // }

        // ChallengeRule selectedRule = weightedRandom(rules);

        // long target = ThreadLocalRandom.current()
        // .nextLong(
        // selectedRule.getMinTarget(),
        // selectedRule.getMaxTarget() + 1);

        // String title = selectedRule.getTitleTemplate()
        // .replace("{x}", String.valueOf(target));

        // String reward = String.valueOf(
        // target * selectedRule.getRewardMultiplier());

        // return ChallengeDefinition.builder()
        // .id(UUID.randomUUID())
        // .gameId(gameId)
        // .createdBy(UUID.randomUUID()) // SYSTEM USER LATER
        // .creatorType(CreatorType.SYSTEM)
        // .title(title)
        // .description(title)
        // .challengeType(challengeType)
        // .eventType(selectedRule.getEventType())
        // .targetValue(target)
        // .conditions(selectedRule.getConditions())
        // .rewardType(RewardType.COINS)
        // .rewardValue(reward)
        // .visibility(Visibility.PUBLIC)
        // .status(ChallengeStatus.ACTIVE)
        // .featured(false)
        // .rarityWeight(selectedRule.getWeight())
        // .startsAt(Instant.now())
        // .endsAt(
        // challengeType == ChallengeType.DAILY
        // ? Instant.now().plus(1, ChronoUnit.DAYS)
        // : Instant.now().plus(7, ChronoUnit.DAYS))
        // .build();
        // }

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

                // Daily -> smaller reward
                // Weekly -> bigger reward
                long rewardCoins = target * selectedRule.getRewardMultiplier();

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
                                .conditions(selectedRule.getConditions())
                                .rewardType(RewardType.COINS)
                                .rewardValue(String.valueOf(rewardCoins))
                                .visibility(Visibility.PUBLIC)
                                .status(ChallengeStatus.ACTIVE)
                                .featured(false)
                                .rarityWeight(selectedRule.getWeight())
                                .startsAt(Instant.now())
                                .endsAt(
                                                challengeType == ChallengeType.DAILY
                                                                ? Instant.now().plus(1, ChronoUnit.DAYS)
                                                                : Instant.now().plus(7, ChronoUnit.DAYS))
                                .build();
        }

        /**
         * Weighted random selection
         */
        private ChallengeRule weightedRandom(
                        List<ChallengeRule> rules) {

                int totalWeight = rules.stream()
                                .mapToInt(ChallengeRule::getWeight)
                                .sum();

                int random = ThreadLocalRandom.current()
                                .nextInt(totalWeight);

                int current = 0;

                for (ChallengeRule rule : rules) {

                        current += rule.getWeight();

                        if (random < current) {
                                return rule;
                        }
                }

                return rules.get(0);
        }

        public List<ChallengeDefinition> generateChallenges(Long gameId) {

                List<ChallengeDefinition> challenges = new ArrayList<>();

                // 2 easy daily challenges
                challenges.add(
                                generateChallenge(
                                                gameId,
                                                ChallengeType.DAILY,
                                                ChallengeDifficulty.EASY));

                challenges.add(
                                generateChallenge(
                                                gameId,
                                                ChallengeType.DAILY,
                                                ChallengeDifficulty.EASY));

                // 1 hard weekly challenge
                challenges.add(
                                generateChallenge(
                                                gameId,
                                                ChallengeType.WEEKLY,
                                                ChallengeDifficulty.HARD));

                return challenges;
        }
}