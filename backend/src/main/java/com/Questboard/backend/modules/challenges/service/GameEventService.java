package com.Questboard.backend.modules.challenges.service;

import com.Questboard.backend.modules.challenges.dto.CurrentProgressDto;
import com.Questboard.backend.modules.challenges.dto.EventProcessingResult;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.dto.UserChallengeProgressDto;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;

import java.util.List;
import java.util.UUID;

public interface GameEventService {
    List<EventProcessingResult> submitEvent(GameEventDto dto, UUID userId);
    UserChallengeProgress getCurrentProgress(CurrentProgressDto request, UUID userId);
}
