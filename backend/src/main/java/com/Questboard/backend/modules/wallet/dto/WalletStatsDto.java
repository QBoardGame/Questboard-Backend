package com.Questboard.backend.modules.wallet.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletStatsDto {

    private final BigDecimal cashBalance;
    private final Long coinBalance;
    private final BigDecimal lockedCashBalance;
    private final Long lockedCoinBalance;
    private final BigDecimal totalCashEarned;
    private final Long totalCoinsEarned;
}
