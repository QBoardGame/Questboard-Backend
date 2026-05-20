package com.Questboard.backend.modules.challenges.generator;

import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.enums.ChallengeType;
import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomChallengeGenerator {
    private final ChallengeDefinitionRepository definitionRepository;

    public CustomChallengeGenerator(ChallengeDefinitionRepository definitionRepository) {
        this.definitionRepository = definitionRepository;
    }

    public List<ChallengeDefinition> loadActiveCustom(Long gameId) {
        return definitionRepository.findByGameIdAndChallengeType(gameId, ChallengeType.CUSTOM);
    }
}
