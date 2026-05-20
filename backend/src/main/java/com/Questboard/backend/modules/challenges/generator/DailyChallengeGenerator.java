// package com.Questboard.backend.modules.challenges.generator;

// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.entity.UserChallenge;
// import com.Questboard.backend.modules.challenges.enums.ChallengeType;
// import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
// import com.Questboard.backend.modules.challenges.repository.UserChallengeRepository;
// import com.Questboard.backend.modules.challenges.service.UserQueryService;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.Instant;
// import java.time.LocalDate;
// import java.time.ZoneOffset;
// import java.util.List;
// import java.util.UUID;

// @Component
// public class DailyChallengeGenerator {
//     private final ChallengeDefinitionRepository definitionRepository;
//     private final UserChallengeRepository userChallengeRepository;
//     private final UserQueryService userQueryService;

//     public DailyChallengeGenerator(ChallengeDefinitionRepository definitionRepository,
//                                    UserChallengeRepository userChallengeRepository,
//                                    UserQueryService userQueryService) {
//         this.definitionRepository = definitionRepository;
//         this.userChallengeRepository = userChallengeRepository;
//         this.userQueryService = userQueryService;
//     }

//     @Transactional
//     public void generateForGame(Long gameId, int count) {
//         List<ChallengeDefinition> defs = definitionRepository.findByGameIdAndChallengeType(gameId, ChallengeType.DAILY);
//         if (defs.isEmpty()) return;

//         List<UUID> users = userQueryService.getAllActiveUserIds();
//         LocalDate tomorrow = LocalDate.now(ZoneOffset.UTC).plusDays(1);
//         Instant expiresAt = tomorrow.atStartOfDay().toInstant(ZoneOffset.UTC);

//         // Assign first `count` defs to all users deterministically
//         int size = Math.min(count, defs.size());
//         for (UUID userId : users) {
//             for (int i = 0; i < size; i++) {
//                 ChallengeDefinition def = defs.get(i);
//                 UserChallenge uc = UserChallenge.builder()
//                         .id(UUID.randomUUID())
//                         .userId(userId)
//                         .challengeDefinitionId(def.getId())
//                         .progress(0)
//                         .completed(false)
//                         .claimed(false)
//                         .expiresAt(expiresAt)
//                         .createdAt(Instant.now())
//                         .build();
//                 userChallengeRepository.save(uc);
//             }
//         }
//     }
// }
