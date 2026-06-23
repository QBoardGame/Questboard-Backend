package com.Questboard.backend.modules.wallet.dto;

import java.util.UUID;

public record WalletUpdatedEvent(
        UUID userId,
        Long coinBalance) {

}
