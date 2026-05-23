package com.Questboard.backend.modules.challenges.service.impl;

import com.Questboard.backend.modules.challenges.dto.CurrentProgressDto;
import com.Questboard.backend.modules.challenges.dto.GameEventDto;
import com.Questboard.backend.modules.challenges.dto.UserChallengeProgressDto;
import com.Questboard.backend.modules.challenges.entity.GameEvent;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.repository.GameEventRepository;
import com.Questboard.backend.modules.challenges.service.GameEventService;
import com.Questboard.backend.modules.challenges.strategy.ChallengeStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class GameEventServiceImpl implements GameEventService {
    private final GameEventRepository gameEventRepository;
    private final ChallengeStrategyFactory strategyFactory;

    public GameEventServiceImpl(GameEventRepository gameEventRepository,
                                ChallengeStrategyFactory strategyFactory) {
        this.gameEventRepository = gameEventRepository;
        this.strategyFactory = strategyFactory;
    }

    @Override
    @Transactional
    public UUID submitEvent(GameEventDto dto, UUID userId) {
        GameEvent e = GameEvent.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .gameId(dto.getGameId())
                .eventType(dto.getEventType())
                .value(dto.getValue())
                .metadata(dto.getMetadata())
                .createdAt(Instant.now())
                .build();

        gameEventRepository.save(e);

        // delegate to strategy
        // strategyFactory.resolve(dto.getGameId()).process(e);
        strategyFactory.getStrategy(dto.getGameId()).process(dto, userId);

        return e.getId();
    }

    @Override
    public UserChallengeProgress getCurrentProgress(CurrentProgressDto request, UUID userId){
        return strategyFactory.getStrategy(request.getGameId()).extractProgress(request, userId);
    }
}
