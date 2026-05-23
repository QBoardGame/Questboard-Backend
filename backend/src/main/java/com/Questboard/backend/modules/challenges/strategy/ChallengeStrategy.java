// package com.Questboard.backend.modules.challenges.strategy;

// import com.Questboard.backend.modules.challenges.entity.GameEvent;

// public interface ChallengeStrategy {
//     boolean supports(Long gameId);
//     void process(GameEvent event);
// }

package com.Questboard.backend.modules.challenges.strategy;

import com.Questboard.backend.modules.challenges.dto.CurrentProgressDto;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.dto.UserChallengeProgressDto;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;

import java.util.List;
import java.util.UUID;

public interface ChallengeStrategy {

    Long supportedGameId();

    List<ChallengeDefinition> getActiveChallenges(
            Long gameId);

    boolean matches(
            ChallengeDefinition challenge,
            GameEventDto event);

    UserChallengeProgress extractProgress(
        CurrentProgressDto request,
            UUID userId);

    void process(GameEventDto dto, UUID userId);

}
