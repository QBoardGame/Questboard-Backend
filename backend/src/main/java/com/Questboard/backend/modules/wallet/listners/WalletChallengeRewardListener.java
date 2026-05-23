package com.Questboard.backend.modules.wallet.listners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.Questboard.backend.modules.challenges.dto.ChallengeCompletedEvent;
import com.Questboard.backend.modules.challenges.repository.UserChallengeProgressRepository;
import com.Questboard.backend.modules.wallet.enums.TransactionType;
import com.Questboard.backend.modules.wallet.service.WalletService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletChallengeRewardListener {

    private final WalletService walletService;
    private final UserChallengeProgressRepository progressRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChallengeCompleted(ChallengeCompletedEvent event) {

        try {

            // ----------------------------------------
            // 1. Idempotency lock (VERY IMPORTANT)
            // ----------------------------------------
            int locked = progressRepository.lockForReward(event.progressId());

            if (locked == 0) {
                log.info("Reward already processed for progressId={}", event.progressId());
                return;
            }

            // ----------------------------------------
            // 2. Calculate reward (simple example)
            // ----------------------------------------
            long rewardAmount = calculateReward(event.rewardAmount());

            // ----------------------------------------
            // 3. Credit wallet
            // ----------------------------------------
            boolean success = walletService.creditCoins(
                    event.userId(),
                    rewardAmount,
                    TransactionType.CHALLENGE_REWARD,
                    event.progressId(),
                    "Challenge completion reward"
            );

            // ----------------------------------------
            // 4. Mark claimed ONLY if wallet success
            // ----------------------------------------
            if (success) {

                progressRepository.markClaimed(event.progressId());

                log.info("Reward processed successfully for user={}, progressId={}",
                        event.userId(),
                        event.progressId());

            } else {
                log.error("Wallet credit failed for progressId={}", event.progressId());
            }

        } catch (Exception e) {

            log.error("Error processing challenge reward event: {}", event, e);
        }
    }

    // ----------------------------------------
    // SIMPLE REWARD LOGIC (replace later)
    // ----------------------------------------
    private long calculateReward(String rewardValue) {
        return Integer.parseInt(rewardValue); // example logic
    }
}