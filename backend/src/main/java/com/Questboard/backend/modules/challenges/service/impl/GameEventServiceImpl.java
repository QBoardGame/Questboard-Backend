package com.Questboard.backend.modules.challenges.service.impl;

import com.Questboard.backend.modules.challenges.dto.CurrentProgressDto;
import com.Questboard.backend.modules.challenges.dto.EventProcessingResult;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.entity.GameEvent;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.repository.GameEventRepository;
import com.Questboard.backend.modules.challenges.service.GameEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameEventServiceImpl implements GameEventService {
    private final GameEventRepository gameEventRepository;
    private final GenericChallengeProcessor challengeProcessor;

    // @Override
    // @Transactional
    // public UUID submitEvent(GameEventDto dto, UUID userId) {
    // GameEvent event = GameEvent.builder()
    // .id(UUID.randomUUID())
    // .userId(userId)
    // .gameId(dto.getGameId())
    // .eventType(dto.getEventType())
    // .value(dto.getValue())
    // .metadata(dto.getMetadata() != null ? dto.getMetadata().toString() : null)
    // .createdAt(Instant.now())
    // .build();

    // gameEventRepository.save(event);
    // challengeProcessor.process(dto, userId);
    // return event.getId();
    // }

    @Transactional
    public List<EventProcessingResult> submitEvent(
            GameEventDto dto,
            UUID userId) {

        GameEvent event = GameEvent.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .gameId(dto.getGameId())
                .eventType(dto.getEventType())
                .value(dto.getValue())
                .metadata(dto.getMetadata() != null
                        ? dto.getMetadata().toString()
                        : null)
                .createdAt(Instant.now())
                .build();

        gameEventRepository.save(event);

        List<EventProcessingResult> results = challengeProcessor.process(dto, userId);

        results.forEach(r -> r.setEventId(event.getId()));

        return results;
    }

    @Override
    public UserChallengeProgress getCurrentProgress(CurrentProgressDto request, UUID userId) {
        return challengeProcessor.getCurrentProgress(request, userId);
    }
}
