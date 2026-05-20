package com.Questboard.backend.modules.challenges.service;

import java.util.UUID;

import com.Questboard.backend.modules.challenges.enums.RewardType;

public interface RewardService {
    // void grantReward(UUID userId, String amount, UUID userChallengeId);

    void grantReward(
            UUID userId,
            RewardType rewardType,
            String rewardValue,
            UUID challengeId);
}
