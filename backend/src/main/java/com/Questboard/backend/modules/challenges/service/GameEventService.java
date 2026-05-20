package com.Questboard.backend.modules.challenges.service;

import com.Questboard.backend.modules.challenges.dto.GameEventDto;

import java.util.UUID;

public interface GameEventService {
    UUID submitEvent(GameEventDto dto);
}
