package com.Questboard.backend.modules.auth.dto.response;

import java.util.UUID;

import com.Questboard.backend.modules.wallet.dto.WalletStatsDto;
import com.Questboard.backend.modules.wallet.models.Wallet;

import lombok.Builder;

@Builder
public record AuthMeResponse(
        UUID userId,
        String email,
        String role,
        String username,
        WalletStatsDto wallet) {
}