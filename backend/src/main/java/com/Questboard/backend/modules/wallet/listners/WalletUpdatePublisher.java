package com.Questboard.backend.modules.wallet.listners;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.Questboard.backend.infrastructure.GameEventWebSocketHandler;
import com.Questboard.backend.modules.wallet.models.Wallet;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class WalletUpdatePublisher {

    private final GameEventWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public void publish(Wallet wallet) {

        try {
            Map<String, Object> payload = Map.of(
                "type", "wallet_update",
                "cashBalance", wallet.getCashBalance(),
                "coinBalance", wallet.getCoinBalance(),
                "lockedCashBalance", wallet.getLockedCashBalance(),
                "lockedCoinBalance", wallet.getLockedCoinBalance(),
                "totalCashEarned", wallet.getTotalCashEarned(),
                "totalCoinsEarned", wallet.getTotalCoinsEarned()
            );

            webSocketHandler.sendToUser(
                wallet.getUser().getId(),
                objectMapper.writeValueAsString(payload)
            );

        } catch (Exception e) {
            // log error
        }
    }
}