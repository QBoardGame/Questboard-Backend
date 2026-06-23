// src/main/java/com/techienello/questboard/modules/tracking/dto/GameEventRequest.java
package com.Questboard.backend.infrastructure;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameEventRequest {

    private UUID userId;

    private String gameSlug;

    private Long gameId;

    private String eventType;

    private String eventTimestamp;

    private Long count;

    private Map<String, Object> metadata;

    private Map<String, Object> rawData;
}