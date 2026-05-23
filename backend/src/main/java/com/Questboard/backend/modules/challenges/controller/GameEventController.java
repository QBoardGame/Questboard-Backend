package com.Questboard.backend.modules.challenges.controller;

import com.Questboard.backend.modules.auth.dto.JwtUserPrincipal;
import com.Questboard.backend.modules.challenges.dto.CurrentProgressDto;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.service.GameEventService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class GameEventController {

    private final GameEventService gameEventService;

    @PostMapping
    public void processEvent(
            @RequestBody GameEventDto event,
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        gameEventService.submitEvent(event, principal.getUserId());
    }

    @GetMapping
    public UserChallengeProgress getCurrentProcess(
            @RequestBody CurrentProgressDto request,
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        return gameEventService.getCurrentProgress(request, principal.getUserId());
    }

}
