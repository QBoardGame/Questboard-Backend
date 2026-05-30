package com.Questboard.backend.modules.challenges.controller;

import com.Questboard.backend.modules.auth.dto.JwtUserPrincipal;
import com.Questboard.backend.modules.challenges.dto.ChallengeWithProgressDto;
import com.Questboard.backend.modules.challenges.dto.ParticipationResponse;
import com.Questboard.backend.modules.challenges.service.ChallengeService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{challengeId}/join")
    public ResponseEntity<?> joinChallenge(
            @PathVariable UUID challengeId,
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        ParticipationResponse response = challengeService.joinChallenge(principal.getUserId(), challengeId);

        return ResponseEntity.ok(response);
    }

}
