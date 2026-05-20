package com.Questboard.backend.modules.challenges.controller;

import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.service.GameEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class GameEventController {

    private final GameEventService gameEventService;

    @PostMapping
    public void processEvent(
            @RequestBody GameEventDto event) {
        gameEventService.submitEvent(event);
    }

}
