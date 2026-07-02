package com.Questboard.backend.modules.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Questboard.backend.modules.auth.model.PolicyAcceptance;
import com.Questboard.backend.modules.auth.model.User;

public interface PolicyAcceptanceRepository
        extends JpaRepository<PolicyAcceptance, Long> {

    Optional<PolicyAcceptance> findTopByUserOrderByAcceptedAtDesc(User user);
}
