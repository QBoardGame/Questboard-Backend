// package com.Questboard.backend.modules.challenges.service;

// import com.Questboard.backend.modules.challenges.dto.UserChallengeDto;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// import java.util.UUID;

// public interface ChallengeService {
//     Page<UserChallengeDto> getUserChallenges(UUID userId, Pageable pageable);
//     void assignChallengeToUser(UUID userId, UUID challengeDefinitionId);
//     void claimReward(UUID userId, UUID userChallengeId);
// }

package com.Questboard.backend.modules.challenges.service;

import com.Questboard.backend.modules.challenges.dto.ChallengeWithProgressDto;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {

    List<ChallengeWithProgressDto> getActiveChallenges(
            Long gameId,
            UUID userId);

    void claimReward(
            UUID userId,
            UUID challengeId);

}
