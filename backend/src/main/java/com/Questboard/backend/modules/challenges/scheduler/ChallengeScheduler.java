package com.Questboard.backend.modules.challenges.scheduler;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.Questboard.backend.modules.challenges.adapter.GameEventAdapter;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.enums.*;
import com.Questboard.backend.modules.challenges.generator.ChallengeGeneratorService;
import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class ChallengeScheduler {

//         private final ChallengeGeneratorService generatorService;
//         private final ChallengeDefinitionRepository challengeRepository;

//         private static final Long VALORANT_GAME_ID = 21640L;
//         private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

//         /**
//          * RUN ON APPLICATION START
//          */
//         @EventListener(ApplicationReadyEvent.class)
//         public void onApplicationReady() {

//                 log.info("🚀 Challenge Scheduler started");

//                 try {
//                         generateDailyChallenges();
//                         generateWeeklyChallenges();
//                 } catch (Exception e) {
//                         log.error("❌ Error during startup challenge generation", e);
//                 }

//                 log.info("✅ Initial challenge generation completed");
//         }

//         /**
//          * DAILY CHALLENGES
//          * Generates 2 EASY daily challenges
//          */
//         @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
//         @Transactional
//         public void generateDailyChallenges() {

//                 log.info("📅 DAILY challenge generation started");

//                 ZonedDateTime now = ZonedDateTime.now(ZONE);

//                 Instant dayStart = now.toLocalDate()
//                                 .atStartOfDay(ZONE)
//                                 .toInstant();

//                 Instant dayEnd = now.toLocalDate()
//                                 .plusDays(1)
//                                 .atStartOfDay(ZONE)
//                                 .minusSeconds(1)
//                                 .toInstant();

//                 log.info("📌 DAILY window: {} → {}", dayStart, dayEnd);

//                 boolean exists = challengeRepository
//                                 .existsByGameIdAndChallengeTypeAndStartsAtBetween(
//                                                 VALORANT_GAME_ID,
//                                                 ChallengeType.DAILY,
//                                                 dayStart,
//                                                 dayEnd);

//                 if (exists) {
//                         log.warn("⚠️ DAILY challenges already exist. Skipping generation.");
//                         return;
//                 }

//                 // Prevent duplicate challenge titles
//                 Set<String> generatedTitles = new HashSet<>();

//                 int created = 0;

//                 while (created < 2) {

//                         ChallengeDefinition challenge = generatorService.generateChallenge(
//                                         VALORANT_GAME_ID,
//                                         ChallengeType.DAILY,
//                                         ChallengeDifficulty.EASY);

//                         // Skip duplicates
//                         if (generatedTitles.contains(challenge.getTitle())) {
//                                 continue;
//                         }

//                         generatedTitles.add(challenge.getTitle());

//                         challenge.setStartsAt(dayStart);
//                         challenge.setEndsAt(dayEnd);
//                         challenge.setStatus(ChallengeStatus.ACTIVE);

//                         challengeRepository.save(challenge);

//                         created++;

//                         log.info(
//                                         "✅ DAILY challenge {} created: {}",
//                                         created,
//                                         challenge.getTitle());
//                 }

//                 log.info("🎯 DAILY challenge generation completed");
//         }

//         /**
//          * WEEKLY CHALLENGE
//          * Generates 1 HARD weekly challenge
//          */
//         @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Kolkata")
//         @Transactional
//         public void generateWeeklyChallenges() {

//                 log.info("📅 WEEKLY challenge generation started");

//                 ZonedDateTime now = ZonedDateTime.now(ZONE);

//                 ZonedDateTime weekStart = now
//                                 .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//                                 .toLocalDate()
//                                 .atStartOfDay(ZONE);

//                 ZonedDateTime weekEnd = weekStart
//                                 .plusDays(6)
//                                 .withHour(23)
//                                 .withMinute(59)
//                                 .withSecond(59);

//                 Instant start = weekStart.toInstant();
//                 Instant end = weekEnd.toInstant();

//                 log.info("📌 WEEKLY window: {} → {}", start, end);

//                 boolean exists = challengeRepository
//                                 .existsByGameIdAndChallengeTypeAndStartsAtBetween(
//                                                 VALORANT_GAME_ID,
//                                                 ChallengeType.WEEKLY,
//                                                 start,
//                                                 end);

//                 if (exists) {
//                         log.warn("⚠️ WEEKLY challenge already exists. Skipping generation.");
//                         return;
//                 }

//                 ChallengeDefinition challenge = generatorService.generateChallenge(
//                                 VALORANT_GAME_ID,
//                                 ChallengeType.WEEKLY,
//                                 ChallengeDifficulty.HARD);

//                 challenge.setStartsAt(start);
//                 challenge.setEndsAt(end);
//                 challenge.setStatus(ChallengeStatus.ACTIVE);

//                 challengeRepository.save(challenge);

//                 log.info("🏆 WEEKLY challenge created: {}", challenge.getTitle());

//                 log.info("🎯 WEEKLY challenge generation completed");
//         }
// }

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeScheduler {

        private final ChallengeGeneratorService generatorService;
        private final ChallengeDefinitionRepository challengeRepository;
        private final List<GameEventAdapter> adapters;

        private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

        @EventListener(ApplicationReadyEvent.class)
        public void onApplicationReady() {

                log.info("🚀 Challenge Scheduler started");

                try {
                        generateDailyChallenges();
                        generateWeeklyChallenges();
                } catch (Exception e) {
                        log.error("❌ Error during startup generation", e);
                }

                log.info("✅ Initial challenge generation completed");
        }

        @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
        @Transactional
        public void generateDailyChallenges() {

                log.info("📅 DAILY challenge generation started");

                ZonedDateTime now = ZonedDateTime.now(ZONE);

                Instant dayStart = now.toLocalDate()
                                .atStartOfDay(ZONE)
                                .toInstant();

                Instant dayEnd = now.toLocalDate()
                                .plusDays(1)
                                .atStartOfDay(ZONE)
                                .minusSeconds(1)
                                .toInstant();

                for (GameEventAdapter adapter : adapters) {

                        Long gameId = adapter.supportedGameId();

                        log.info("🎮 Generating DAILY challenges for gameId={}", gameId);

                        boolean exists = challengeRepository
                                        .existsByGameIdAndChallengeTypeAndStartsAtBetween(
                                                        gameId,
                                                        ChallengeType.DAILY,
                                                        dayStart,
                                                        dayEnd);

                        if (exists) {
                                log.warn("⚠️ DAILY already exists for gameId={}", gameId);
                                continue;
                        }

                        Set<String> titles = new HashSet<>();
                        int created = 0;

                        while (created < 2) {

                                ChallengeDefinition challenge = generatorService.generateChallenge(
                                                gameId,
                                                ChallengeType.DAILY,
                                                ChallengeDifficulty.EASY);

                                if (titles.contains(challenge.getTitle())) {
                                        continue;
                                }

                                titles.add(challenge.getTitle());

                                challenge.setStartsAt(dayStart);
                                challenge.setEndsAt(dayEnd);
                                challenge.setStatus(ChallengeStatus.ACTIVE);

                                challengeRepository.save(challenge);

                                created++;

                                log.info("✅ DAILY challenge created for gameId={}: {}",
                                                gameId,
                                                challenge.getTitle());
                        }
                }

                log.info("🎯 DAILY generation completed for all games");
        }

        @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Kolkata")
        @Transactional
        public void generateWeeklyChallenges() {

                log.info("📅 WEEKLY challenge generation started");

                ZonedDateTime now = ZonedDateTime.now(ZONE);

                ZonedDateTime weekStart = now
                                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                .toLocalDate()
                                .atStartOfDay(ZONE);

                ZonedDateTime weekEnd = weekStart
                                .plusDays(6)
                                .withHour(23)
                                .withMinute(59)
                                .withSecond(59);

                Instant start = weekStart.toInstant();
                Instant end = weekEnd.toInstant();

                for (GameEventAdapter adapter : adapters) {

                        Long gameId = adapter.supportedGameId();

                        log.info("🎮 Generating WEEKLY challenge for gameId={}", gameId);

                        boolean exists = challengeRepository
                                        .existsByGameIdAndChallengeTypeAndStartsAtBetween(
                                                        gameId,
                                                        ChallengeType.WEEKLY,
                                                        start,
                                                        end);

                        if (exists) {
                                log.warn("⚠️ WEEKLY already exists for gameId={}", gameId);
                                continue;
                        }

                        ChallengeDefinition challenge = generatorService.generateChallenge(
                                        gameId,
                                        ChallengeType.WEEKLY,
                                        ChallengeDifficulty.HARD);

                        challenge.setStartsAt(start);
                        challenge.setEndsAt(end);
                        challenge.setStatus(ChallengeStatus.ACTIVE);

                        challengeRepository.save(challenge);

                        log.info("🏆 WEEKLY challenge created for gameId={}", gameId);
                }

                log.info("🎯 WEEKLY generation completed for all games");
        }
}