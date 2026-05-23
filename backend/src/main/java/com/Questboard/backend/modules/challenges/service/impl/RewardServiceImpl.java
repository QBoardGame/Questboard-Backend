package com.Questboard.backend.modules.challenges.service.impl;

import com.Questboard.backend.modules.challenges.enums.RewardType;
import com.Questboard.backend.modules.challenges.repository.UserChallengeProgressRepository;
import com.Questboard.backend.modules.challenges.service.RewardService;
import com.Questboard.backend.modules.wallet.enums.TransactionType;
import com.Questboard.backend.modules.wallet.service.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final UserChallengeProgressRepository progressRepository;
    private final WalletService walletService;

    @Override
    @Transactional
    public void grantReward(
            UUID userId,
            RewardType rewardType,
            String rewardValue,
            UUID challengeId
    ) {

        // 1. Validate duplicate reward
        var progress = progressRepository
                .findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() ->
                        new RuntimeException("Progress not found"));

        if (progress.isClaimed()) {
            log.warn("Reward already claimed for user {} challenge {}",
                    userId, challengeId);
            return;
        }

        // 2. Apply reward based on type
        switch (rewardType) {

            case COINS -> addCoins(userId, Long.parseLong(rewardValue), challengeId);

            case XP -> addXp(userId, Long.parseLong(rewardValue));

            case CASH -> addCash(userId, Double.parseDouble(rewardValue));

            case ITEM -> addItem(userId, rewardValue);

            default -> throw new RuntimeException("Unknown reward type");
        }

        // 3. Mark as claimed
        progress.setClaimed(true);
        progressRepository.save(progress);

        log.info("Reward granted: {} {} to user {}",
                rewardType, rewardValue, userId);
    }

    // -----------------------------
    // INTERNAL WALLET METHODS
    // -----------------------------

    private void addCoins(UUID userId, long amount, UUID challengeId) {
        walletService.creditCoins(userId, amount, TransactionType.CHALLENGE_REWARD, challengeId, "User get this after completing challenge");
        log.info("Added {} coins to user {}", amount, userId);
    }

    private void addXp(UUID userId, long amount) {
        // TODO integrate XP system
        log.info("Added {} XP to user {}", amount, userId);
    }

    private void addCash(UUID userId, double amount) {
        // TODO integrate payment/wallet system
        log.info("Added {} cash to user {}", amount, userId);
    }

    private void addItem(UUID userId, String itemId) {
        // TODO inventory system
        log.info("Granted item {} to user {}", itemId, userId);
    }
}