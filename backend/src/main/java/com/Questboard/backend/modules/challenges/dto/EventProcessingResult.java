package com.Questboard.backend.modules.challenges.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventProcessingResult {

    private UUID eventId;

    private Long gameId;

    private UUID challengeId;

    private Long progress;

    private Long targetValue;

    private boolean completed;

    private boolean claimed;
}
