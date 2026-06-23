package com.Questboard.backend.modules.wallet.listners;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.Questboard.backend.infrastructure.GameEventWebSocketHandler;
import com.Questboard.backend.modules.wallet.dto.WalletUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletEventListener {

    private final GameEventWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleWalletUpdate(WalletUpdatedEvent event) {

        try {

            Map<String, Object> payload = Map.of(
                    "type", "wallet_update",
                    "coinBalance", event.coinBalance()
            );

            webSocketHandler.sendToUser(
                    event.userId(),
                    objectMapper.writeValueAsString(payload)
            );

        } catch (Exception e) {
            log.error("Failed to send wallet update event", e);
        }
    }
}
