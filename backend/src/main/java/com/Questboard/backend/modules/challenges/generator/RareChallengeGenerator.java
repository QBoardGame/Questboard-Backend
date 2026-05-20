// package com.Questboard.backend.modules.challenges.generator;

// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.enums.ChallengeType;
// import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
// import org.springframework.stereotype.Component;

// import java.util.List;
// import java.util.Optional;

// @Component
// public class RareChallengeGenerator {
//     private final ChallengeDefinitionRepository definitionRepository;

//     public RareChallengeGenerator(ChallengeDefinitionRepository definitionRepository) {
//         this.definitionRepository = definitionRepository;
//     }

//     public Optional<ChallengeDefinition> pickRare(Long gameId) {
//         List<ChallengeDefinition> defs = definitionRepository.findByGameIdAndChallengeType(gameId, ChallengeType.RARE);
//         if (defs.isEmpty()) return Optional.empty();

//         // simple weighted pick
//         int total = defs.stream().mapToInt(d -> d.getRarityWeight() == null ? 1 : d.getRarityWeight()).sum();
//         int r = (int) (Math.random() * total);
//         int acc = 0;
//         for (ChallengeDefinition d : defs) {
//             acc += d.getRarityWeight() == null ? 1 : d.getRarityWeight();
//             if (r < acc) return Optional.of(d);
//         }
//         return Optional.of(defs.get(0));
//     }
// }
