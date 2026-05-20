package com.Questboard.backend.modules.challenges.repository;

import com.Questboard.backend.modules.challenges.entity.GameEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameEventRepository extends JpaRepository<GameEvent, UUID> {
}
