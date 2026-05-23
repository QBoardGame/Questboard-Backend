// package com.Questboard.backend.modules.challenges.scheduler;

// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
// import com.Questboard.backend.modules.challenges.enums.ChallengeType;
// import com.Questboard.backend.modules.challenges.generator.ChallengeGeneratorService;
// import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;

// import lombok.RequiredArgsConstructor;

// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.*;
// import java.time.temporal.TemporalAdjusters;

// @Component
// @RequiredArgsConstructor
// public class ChallengeScheduler {

//     private final ChallengeGeneratorService generatorService;
//     private final ChallengeDefinitionRepository challengeRepository;

//     /**
//      * Valorant Game ID
//      */
//     private static final Long VALORANT_GAME_ID = 21640L;

//     /**
//      * DAILY CHALLENGES
//      *
//      * Runs every day at 12:00 AM
//      * Generates 2 daily challenges
//      */
//     @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
//     @Transactional
//     public void generateDailyChallenges() {

//         Instant now = Instant.now();

//         ZonedDateTime istNow = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

//         Instant dayStart = istNow.toLocalDate()
//                 .atStartOfDay(ZoneId.of("Asia/Kolkata"))
//                 .toInstant();

//         Instant dayEnd = istNow.toLocalDate()
//                 .plusDays(1)
//                 .atStartOfDay(ZoneId.of("Asia/Kolkata"))
//                 .minusSeconds(1)
//                 .toInstant();

//         /**
//          * Prevent duplicate generation
//          */
//         boolean alreadyGenerated = challengeRepository.existsByGameIdAndChallengeTypeAndStartsAtBetween(
//                 VALORANT_GAME_ID,
//                 ChallengeType.DAILY,
//                 dayStart,
//                 dayEnd);

//         if (alreadyGenerated) {
//             return;
//         }

//         /**
//          * Generate 2 random daily challenges
//          */
//         for (int i = 0; i < 2; i++) {

//             ChallengeDefinition challenge = generatorService.generateChallenge(
//                     VALORANT_GAME_ID,
//                     ChallengeType.DAILY);

//             challenge.setStartsAt(dayStart);
//             challenge.setEndsAt(dayEnd);
//             challenge.setStatus(ChallengeStatus.ACTIVE);

//             challengeRepository.save(challenge);
//         }
//     }

//     /**
//      * WEEKLY CHALLENGES
//      *
//      * Runs every Monday at 12:00 AM
//      * Challenge duration:
//      * Monday → Sunday
//      */
//     @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Kolkata")
//     @Transactional
//     public void generateWeeklyChallenges() {

//         ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

//         /**
//          * Monday 00:00:00
//          */
//         ZonedDateTime weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//                 .toLocalDate()
//                 .atStartOfDay(ZoneId.of("Asia/Kolkata"));

//         /**
//          * Sunday 23:59:59
//          */
//         ZonedDateTime weekEnd = weekStart.plusDays(6)
//                 .withHour(23)
//                 .withMinute(59)
//                 .withSecond(59);

//         Instant startInstant = weekStart.toInstant();
//         Instant endInstant = weekEnd.toInstant();

//         /**
//          * Prevent duplicate weekly generation
//          */
//         boolean alreadyGenerated = challengeRepository.existsByGameIdAndChallengeTypeAndStartsAtBetween(
//                 VALORANT_GAME_ID,
//                 ChallengeType.WEEKLY,
//                 startInstant,
//                 endInstant);

//         if (alreadyGenerated) {
//             return;
//         }

//         /**
//          * Generate 1 weekly challenge
//          */
//         ChallengeDefinition challenge = generatorService.generateChallenge(
//                 VALORANT_GAME_ID,
//                 ChallengeType.WEEKLY);

//         challenge.setStartsAt(startInstant);
//         challenge.setEndsAt(endInstant);
//         challenge.setStatus(ChallengeStatus.ACTIVE);

//         challengeRepository.save(challenge);
//     }
// }

// package com.Questboard.backend.modules.challenges.scheduler;

// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
// import com.Questboard.backend.modules.challenges.enums.ChallengeType;
// import com.Questboard.backend.modules.challenges.generator.ChallengeGeneratorService;
// import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.context.event.EventListener;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.boot.context.event.ApplicationReadyEvent;

// import java.time.*;
// import java.time.temporal.TemporalAdjusters;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class ChallengeScheduler {

//     private final ChallengeGeneratorService generatorService;
//     private final ChallengeDefinitionRepository challengeRepository;

//     private static final Long VALORANT_GAME_ID = 21640L;
//     private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

//     /**
//      * RUN ON APPLICATION START
//      */
//     @EventListener(ApplicationReadyEvent.class)
//     public void onApplicationReady() {

//         log.info("🚀 Challenge Scheduler started (Application Ready Event)");

//         try {
//             generateDailyChallenges();
//             generateWeeklyChallenges();
//         } catch (Exception e) {
//             log.error("❌ Error during startup challenge generation", e);
//         }

//         log.info("✅ Initial challenge generation completed on startup");
//     }

//     /**
//      * DAILY CHALLENGES
//      * Runs every day at 12:00 AM
//      */
//     @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
//     @Transactional
//     public void generateDailyChallenges() {

//         log.info("📅 DAILY challenge generation started");

//         ZonedDateTime now = ZonedDateTime.now(ZONE);

//         Instant dayStart = now.toLocalDate()
//                 .atStartOfDay(ZONE)
//                 .toInstant();

//         Instant dayEnd = now.toLocalDate()
//                 .plusDays(1)
//                 .atStartOfDay(ZONE)
//                 .minusSeconds(1)
//                 .toInstant();

//         log.info("📌 DAILY window: {} → {}", dayStart, dayEnd);

//         boolean exists = challengeRepository
//                 .existsByGameIdAndChallengeTypeAndStartsAtBetween(
//                         VALORANT_GAME_ID,
//                         ChallengeType.DAILY,
//                         dayStart,
//                         dayEnd
//                 );

//         if (exists) {
//             log.warn("⚠️ DAILY challenges already exist. Skipping generation.");
//             return;
//         }

//         for (int i = 0; i < 2; i++) {

//             ChallengeDefinition challenge =
//                     generatorService.generateChallenge(
//                             VALORANT_GAME_ID,
//                             ChallengeType.DAILY
//                     );

//             challenge.setStartsAt(dayStart);
//             challenge.setEndsAt(dayEnd);
//             challenge.setStatus(ChallengeStatus.ACTIVE);

//             challengeRepository.save(challenge);

//             log.info("✅ DAILY challenge {} created: {}",
//                     i + 1,
//                     challenge.getTitle());
//         }

//         log.info("🎯 DAILY challenge generation completed");
//     }

//     /**
//      * WEEKLY CHALLENGES
//      * Runs every Monday at 12:00 AM
//      * Week: Monday → Sunday
//      */
//     @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Kolkata")
//     @Transactional
//     public void generateWeeklyChallenges() {

//         log.info("📅 WEEKLY challenge generation started");

//         ZonedDateTime now = ZonedDateTime.now(ZONE);

//         ZonedDateTime weekStart = now
//                 .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//                 .toLocalDate()
//                 .atStartOfDay(ZONE);

//         ZonedDateTime weekEnd = weekStart
//                 .plusDays(6)
//                 .withHour(23)
//                 .withMinute(59)
//                 .withSecond(59);

//         Instant start = weekStart.toInstant();
//         Instant end = weekEnd.toInstant();

//         log.info("📌 WEEKLY window: {} → {}", start, end);

//         boolean exists = challengeRepository
//                 .existsByGameIdAndChallengeTypeAndStartsAtBetween(
//                         VALORANT_GAME_ID,
//                         ChallengeType.WEEKLY,
//                         start,
//                         end
//                 );

//         if (exists) {
//             log.warn("⚠️ WEEKLY challenge already exists. Skipping generation.");
//             return;
//         }

//         ChallengeDefinition challenge =
//                 generatorService.generateChallenge(
//                         VALORANT_GAME_ID,
//                         ChallengeType.WEEKLY
//                 );

//         challenge.setStartsAt(start);
//         challenge.setEndsAt(end);
//         challenge.setStatus(ChallengeStatus.ACTIVE);

//         challengeRepository.save(challenge);

//         log.info("🏆 WEEKLY challenge created: {}", challenge.getTitle());
//         log.info("🎯 WEEKLY challenge generation completed");
//     }
// } 

package com.Questboard.backend.modules.challenges.scheduler;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.enums.*;
import com.Questboard.backend.modules.challenges.generator.ChallengeGeneratorService;
import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeScheduler {

        private final ChallengeGeneratorService generatorService;
        private final ChallengeDefinitionRepository challengeRepository;

        private static final Long VALORANT_GAME_ID = 21640L;
        private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

        /**
         * RUN ON APPLICATION START
         */
        @EventListener(ApplicationReadyEvent.class)
        public void onApplicationReady() {

                log.info("🚀 Challenge Scheduler started");

                try {
                        generateDailyChallenges();
                        generateWeeklyChallenges();
                } catch (Exception e) {
                        log.error("❌ Error during startup challenge generation", e);
                }

                log.info("✅ Initial challenge generation completed");
        }

        /**
         * DAILY CHALLENGES
         * Generates 2 EASY daily challenges
         */
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

                log.info("📌 DAILY window: {} → {}", dayStart, dayEnd);

                boolean exists = challengeRepository
                                .existsByGameIdAndChallengeTypeAndStartsAtBetween(
                                                VALORANT_GAME_ID,
                                                ChallengeType.DAILY,
                                                dayStart,
                                                dayEnd);

                if (exists) {
                        log.warn("⚠️ DAILY challenges already exist. Skipping generation.");
                        return;
                }

                // Prevent duplicate challenge titles
                Set<String> generatedTitles = new HashSet<>();

                int created = 0;

                while (created < 2) {

                        ChallengeDefinition challenge = generatorService.generateChallenge(
                                        VALORANT_GAME_ID,
                                        ChallengeType.DAILY,
                                        ChallengeDifficulty.EASY);

                        // Skip duplicates
                        if (generatedTitles.contains(challenge.getTitle())) {
                                continue;
                        }

                        generatedTitles.add(challenge.getTitle());

                        challenge.setStartsAt(dayStart);
                        challenge.setEndsAt(dayEnd);
                        challenge.setStatus(ChallengeStatus.ACTIVE);

                        challengeRepository.save(challenge);

                        created++;

                        log.info(
                                        "✅ DAILY challenge {} created: {}",
                                        created,
                                        challenge.getTitle());
                }

                log.info("🎯 DAILY challenge generation completed");
        }

        /**
         * WEEKLY CHALLENGE
         * Generates 1 HARD weekly challenge
         */
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

                log.info("📌 WEEKLY window: {} → {}", start, end);

                boolean exists = challengeRepository
                                .existsByGameIdAndChallengeTypeAndStartsAtBetween(
                                                VALORANT_GAME_ID,
                                                ChallengeType.WEEKLY,
                                                start,
                                                end);

                if (exists) {
                        log.warn("⚠️ WEEKLY challenge already exists. Skipping generation.");
                        return;
                }

                ChallengeDefinition challenge = generatorService.generateChallenge(
                                VALORANT_GAME_ID,
                                ChallengeType.WEEKLY,
                                ChallengeDifficulty.HARD);

                challenge.setStartsAt(start);
                challenge.setEndsAt(end);
                challenge.setStatus(ChallengeStatus.ACTIVE);

                challengeRepository.save(challenge);

                log.info("🏆 WEEKLY challenge created: {}", challenge.getTitle());

                log.info("🎯 WEEKLY challenge generation completed");
        }
}