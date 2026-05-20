package com.Questboard.backend.modules.challenges.strategy;

import com.Questboard.backend.modules.challenges.exception.StrategyNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// @Component
// public class ChallengeStrategyFactory {
//     private final List<ChallengeStrategy> strategies;

//     public ChallengeStrategyFactory(List<ChallengeStrategy> strategies) {
//         this.strategies = strategies;
//     }

    // public ChallengeStrategy resolve(Long gameId) {
    //     return strategies.stream()
    //             .filter(s -> s.supports(gameId))
    //             .findFirst()
    //             .orElseThrow(() -> new StrategyNotFoundException("No strategy for gameId: " + gameId));
    // }
// }

@Component
public class ChallengeStrategyFactory {

    private final Map<Long, ChallengeStrategy> strategies;

    public ChallengeStrategyFactory(
            List<ChallengeStrategy> strategyList) {

        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        ChallengeStrategy::supportedGameId,
                        s -> s));
    }

    public ChallengeStrategy getStrategy(
            Long gameId) {

        ChallengeStrategy strategy = strategies.get(gameId);

        if (strategy == null) {
            throw new RuntimeException(
                    "No strategy found for game: " + gameId);
        }

        return strategy;
    }

}
