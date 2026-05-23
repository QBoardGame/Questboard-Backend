package com.Questboard.backend.modules.challenges.dto;

import com.Questboard.backend.modules.challenges.enums.EventType;
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
    private String metadata;
    private Long count;
}
