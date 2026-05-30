package com.Questboard.backend.modules.challenges.dto;

import com.Questboard.backend.modules.challenges.enums.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEventDto {
    private Long gameId;
    private EventType eventType;
    private Long value;
    private JsonNode metadata;
    private Long count;
}
