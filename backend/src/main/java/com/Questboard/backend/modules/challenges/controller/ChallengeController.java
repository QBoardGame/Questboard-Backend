// package com.Questboard.backend.modules.challenges.controller;

// import com.Questboard.backend.modules.challenges.dto.GameEventDto;
// import com.Questboard.backend.modules.challenges.dto.UserChallengeDto;
// import com.Questboard.backend.modules.challenges.service.ChallengeService;
// import com.Questboard.backend.modules.challenges.service.GameEventService;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.UUID;

// @RestController
// @RequestMapping("/api/challenges")
// public class ChallengeController {
//     private final ChallengeService challengeService;
//     private final GameEventService gameEventService;

//     public ChallengeController(ChallengeService challengeService, GameEventService gameEventService) {
//         this.challengeService = challengeService;
//         this.gameEventService = gameEventService;
//     }

//     @GetMapping("/daily")
//     public Page<UserChallengeDto> getDaily(@RequestParam UUID userId, Pageable pageable) {
//         return challengeService.getUserChallenges(userId, pageable);
//     }

//     @PostMapping("/events")
//     public ResponseEntity<UUID> submitEvent(@RequestBody GameEventDto dto) {
//         UUID id = gameEventService.submitEvent(dto);
//         return ResponseEntity.ok(id);
//     }

//     @PostMapping("/{id}/claim")
//     public ResponseEntity<Void> claim(@PathVariable("id") UUID id, @RequestParam UUID userId) {
//         challengeService.claimReward(userId, id);
//         return ResponseEntity.ok().build();
//     }

//     @GetMapping("/history")
//     public Page<UserChallengeDto> history(@RequestParam UUID userId, Pageable pageable) {
//         return challengeService.getUserChallenges(userId, pageable);
//     }
// }

package com.Questboard.backend.modules.challenges.controller;

import com.Questboard.backend.modules.auth.dto.JwtUserPrincipal;
import com.Questboard.backend.modules.challenges.dto.ChallengeWithProgressDto;
import com.Questboard.backend.modules.challenges.service.ChallengeService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    /**
     * Get active challenges for a game
     */
    @GetMapping("/games/{gameId}")
    public List<ChallengeWithProgressDto> getChallenges(
            @PathVariable Long gameId,
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        return challengeService.getActiveChallenges(
                gameId,
                principal.getUserId());
    }

    /**
     * Claim reward
     */
    @PostMapping("/{challengeId}/claim")
    public void claimReward(
            @PathVariable UUID challengeId,
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        challengeService.claimReward(
                principal.getUserId(),
                challengeId);
    }

}
