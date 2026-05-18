// src/main/java/com/techienello/questboard/modules/tracking/dto/GameEventRequest.java
package com.Questboard.backend.infrastructure;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class GameEventRequest {
    private UUID userId;
    private Integer gameId;       // Overwolf Game ID (e.g., 21566 for Valorant)
    private String eventType;     // e.g., "KILL", "MATCH_WIN"
    private String eventTimestamp; // To prevent replay attacks
    private Map<String, Object> rawData; // Entire JSON block from Overwolf GEP
}