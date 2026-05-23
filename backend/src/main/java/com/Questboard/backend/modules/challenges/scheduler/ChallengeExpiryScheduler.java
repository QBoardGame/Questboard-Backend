package com.Questboard.backend.modules.challenges.scheduler;

import java.time.Instant;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeExpiryScheduler {

    private final ChallengeDefinitionRepository challengeRepository;

    /**
     * Run once on application startup
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {

        log.info("🚀 Running expired challenge cleanup on startup");

        deactivateExpiredChallenges();
    }

    /**
     * Runs every day at 12:00 AM
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
    @Transactional
    public void deactivateExpiredChallenges() {

        Instant now = Instant.now();

        int updated = challengeRepository
                .deactivateExpiredChallenges(
                        now,
                        ChallengeStatus.ACTIVE,
                        ChallengeStatus.INACTIVE);

        if (updated > 0) {

            log.info(
                    "✅ {} expired challenges marked as INACTIVE",
                    updated);

        } else {

            log.info("ℹ️ No expired challenges found");
        }
    }
}