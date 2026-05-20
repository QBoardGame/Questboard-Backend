// package com.Questboard.backend.modules.challenges.strategy;

// import com.Questboard.backend.modules.challenges.entity.GameEvent;

// public interface ChallengeStrategy {
//     boolean supports(Long gameId);
//     void process(GameEvent event);
// }

package com.Questboard.backend.modules.challenges.strategy;

import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;

import java.util.List;

public interface ChallengeStrategy {

    Long supportedGameId();

    List<ChallengeDefinition> getActiveChallenges(
            Long gameId);

    boolean matches(
            ChallengeDefinition challenge,
            GameEventDto event);

    long extractProgress(
            GameEventDto event);

    void process(GameEventDto dto);

}
