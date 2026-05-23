package com.Questboard.backend.modules.wallet.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.Questboard.backend.modules.wallet.enums.TransactionType;
import com.Questboard.backend.modules.wallet.enums.WalletCurrency;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallet_transactions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private WalletCurrency currency;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal cashAmount;
    
    private Long coinAmount;
    private Long balanceAfterCoins;

    private String description;

    private UUID referenceId;

    private Instant createdAt;
}
