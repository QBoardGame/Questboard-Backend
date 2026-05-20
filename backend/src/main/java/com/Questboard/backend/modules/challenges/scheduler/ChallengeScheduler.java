// package com.Questboard.backend.modules.challenges.scheduler;

// import com.Questboard.backend.modules.challenges.generator.DailyChallengeGenerator;
// import com.Questboard.backend.modules.challenges.generator.RareChallengeGenerator;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;

// @Component
// public class ChallengeScheduler {
//     private final Logger log = LoggerFactory.getLogger(ChallengeScheduler.class);
//     private final DailyChallengeGenerator dailyGenerator;
//     private final RareChallengeGenerator rareGenerator;

//     public ChallengeScheduler(DailyChallengeGenerator dailyGenerator, RareChallengeGenerator rareGenerator) {
//         this.dailyGenerator = dailyGenerator;
//         this.rareGenerator = rareGenerator;
//     }

//     // run at midnight UTC daily
//     // @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
//     // public void runDailyGeneration() {
//     //     log.info("ChallengeScheduler: running daily generation");
//     //     // Example: generate for game ids 1 and 2; in prod this should be dynamic
//     //     dailyGenerator.generateForGame(1L, 3);
//     //     dailyGenerator.generateForGame(2L, 3);

//     //     // small chance to pick rare challenges and log them
//     //     rareGenerator.pickRare(1L).ifPresent(d -> log.info("Picked rare challenge for game 1: {}", d.getTitle()));
//     // }
// }
