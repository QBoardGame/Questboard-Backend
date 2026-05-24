package com.Questboard.backend.modules.wallet.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.Questboard.backend.modules.wallet.enums.TransactionType;
import com.Questboard.backend.modules.wallet.enums.WalletCurrency;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletTransactionResponseDto {

    private final UUID id;
    private final UUID referenceId;
    private final WalletCurrency currency;
    private final TransactionType type;
    private final BigDecimal cashAmount;
    private final Long coinAmount;
    private final Long balanceAfterCoins;
    private final String description;
    private final Instant createdAt;
}
