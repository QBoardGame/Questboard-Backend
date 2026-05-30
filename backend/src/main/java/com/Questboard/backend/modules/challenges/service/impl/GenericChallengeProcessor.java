package com.Questboard.backend.modules.challenges.service.impl;

import com.Questboard.backend.modules.challenges.adapter.GameEventAdapter;
import com.Questboard.backend.modules.challenges.dto.CurrentProgressDto;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
import com.Questboard.backend.modules.challenges.repository.UserChallengeProgressRepository;
import com.Questboard.backend.modules.challenges.service.RewardService;
import com.Questboard.backend.modules.challenges.engine.RuleEngine;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenericChallengeProcessor {

    private final ChallengeDefinitionRepository challengeRepository;
    private final UserChallengeProgressRepository progressRepository;
    private final RewardService rewardService;
    private final RuleEngine ruleEngine;
    private final Map<Long, GameEventAdapter> adapters;

    public GenericChallengeProcessor(List<GameEventAdapter> adapterList,
                                     ChallengeDefinitionRepository challengeRepository,
                                     UserChallengeProgressRepository progressRepository,
                                     RewardService rewardService,
                                     RuleEngine ruleEngine) {
        this.adapters = adapterList.stream()
                .collect(Collectors.toMap(GameEventAdapter::supportedGameId, Function.identity()));
        this.challengeRepository = challengeRepository;
        this.progressRepository = progressRepository;
        this.rewardService = rewardService;
        this.ruleEngine = ruleEngine;
    }

    @Transactional
    public void process(GameEventDto event, UUID userId) {
        if (event == null) {
            return;
        }

        GameEventAdapter adapter = adapters.get(event.getGameId());
        if (adapter == null) {
            throw new IllegalStateException("No GameEventAdapter found for gameId=" + event.getGameId());
        }

        JsonNode normalizedEvent = adapter.normalize(event);
        Instant now = Instant.now();
        List<ChallengeDefinition> challenges = challengeRepository.findActiveByEventType(
                event.getGameId(),
                event.getEventType(),
                now);

        if (challenges.isEmpty()) {
            return;
        }

        long eventValue = event.getValue() != null ? event.getValue() : 0L;

        for (ChallengeDefinition challenge : challenges) {
            try {
                if (!matches(challenge, normalizedEvent)) {
                    continue;
                }

                if (challenge.getEndsAt() != null && now.isAfter(challenge.getEndsAt())) {
                    continue;
                }

                UserChallengeProgress progress = progressRepository
                        .findByUserIdAndChallengeId(userId, challenge.getId())
                        .orElseGet(() -> createProgress(userId, challenge));

                if (progress.isCompleted()) {
                    continue;
                }

                if (progress.getExpiresAt() != null && now.isAfter(progress.getExpiresAt())) {
                    continue;
                }

                long updatedProgress = progress.getProgress() + eventValue;
                progress.setProgress(updatedProgress);

                if (updatedProgress >= challenge.getTargetValue()) {
                    progress.setCompleted(true);
                    progress.setCompletedAt(now);

                    if (!progress.isClaimed()) {
                        rewardService.grantReward(
                                userId,
                                challenge.getRewardType(),
                                challenge.getRewardValue(),
                                challenge.getId());
                        progress.setClaimed(true);
                    }
                }

                progressRepository.save(progress);
            } catch (Exception e) {
                log.error("Error processing challenge {} for user {}", challenge.getId(), userId, e);
            }
        }
    }

    public UserChallengeProgress getCurrentProgress(CurrentProgressDto request, UUID userId) {
        return progressRepository.findByUserIdAndChallengeIdAndGameIdAndCompleted(
                userId,
                request.getChallengeId(),
                request.getGameId(),
                false);
    }

    private boolean matches(ChallengeDefinition challenge, JsonNode normalizedEvent) {
        if (challenge.getConditions() == null || challenge.getConditions().isEmpty()) {
            return true;
        }

        return ruleEngine.evaluate(normalizedEvent, challenge.getConditions());
    }

    private UserChallengeProgress createProgress(UUID userId, ChallengeDefinition challenge) {
        UserChallengeProgress progress = UserChallengeProgress.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .challengeId(challenge.getId())
                .eventType(challenge.getEventType())
                .progress(0L)
                .gameId(challenge.getGameId())
                .targetValue(challenge.getTargetValue())
                .completed(false)
                .claimed(false)
                .startedAt(Instant.now())
                .expiresAt(challenge.getEndsAt())
                .build();

        return progressRepository.save(progress);
    }
}
