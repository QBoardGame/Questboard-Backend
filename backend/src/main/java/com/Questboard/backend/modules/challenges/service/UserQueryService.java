package com.Questboard.backend.modules.challenges.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public interface UserQueryService {
    List<UUID> getAllActiveUserIds();

    @Service
    @ConditionalOnMissingBean(UserQueryService.class)
    class NoopUserQueryService implements UserQueryService {
        @Override
        public List<UUID> getAllActiveUserIds() {
            return new ArrayList<>();
        }
    }
}
