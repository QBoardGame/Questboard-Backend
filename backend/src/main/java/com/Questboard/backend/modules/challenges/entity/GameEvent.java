package com.Questboard.backend.modules.challenges.entity;

import com.Questboard.backend.modules.challenges.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "game_event", indexes = {
        @Index(name = "idx_game_event_user", columnList = "user_id"),
        @Index(name = "idx_game_event_game", columnList = "game_id"),
        @Index(name = "idx_game_event_type", columnList = "event_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEvent {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "value")
    private Long value;

    @Column(name = "metadata", columnDefinition = "text")
    private String metadata;

    @Column(name = "created_at")
    private Instant createdAt;
}
