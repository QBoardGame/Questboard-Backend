package com.Questboard.backend.modules.wallet.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.Questboard.backend.modules.auth.model.User;
import com.Questboard.backend.modules.wallet.listners.WalletListener;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Entity
@EntityListeners(WalletListener.class)
@Table(name = "wallets", indexes = {
        @Index(name = "idx_wallet_user", columnList = "user_id")
})
@Data
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Withdrawable real money balance
     * Example:
     * INR / USD equivalent
     */
    @Column(name = "cash_balance", nullable = false, precision = 19, scale = 2)
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal cashBalance = BigDecimal.ZERO;

    /**
     * Platform virtual currency
     * Example:
     * QuestCoins
     */
    @Column(name = "coin_balance", nullable = false)
    private Long coinBalance = 0L;

    /**
     * Optional locked cash
     * Example:
     * pending withdrawal
     */
    @Column(name = "locked_cash_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal lockedCashBalance = BigDecimal.ZERO;

    /**
     * Optional locked coins
     * Example:
     * reserved tournament entry
     */
    @Column(name = "locked_coin_balance", nullable = false)
    private Long lockedCoinBalance = 0L;

    /**
     * Lifetime earned analytics
     */
    @Column(name = "total_cash_earned", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCashEarned = BigDecimal.ZERO;

    @Column(name = "total_coins_earned", nullable = false)
    private Long totalCoinsEarned = 0L;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {

        if (cashBalance == null) {
            cashBalance = BigDecimal.ZERO;
        }

        if (coinBalance == null) {
            coinBalance = 0L;
        }

        if (lockedCashBalance == null) {
            lockedCashBalance = BigDecimal.ZERO;
        }

        if (lockedCoinBalance == null) {
            lockedCoinBalance = 0L;
        }

        if (totalCashEarned == null) {
            totalCashEarned = BigDecimal.ZERO;
        }

        if (totalCoinsEarned == null) {
            totalCoinsEarned = 0L;
        }

        if(createdAt == null){
            createdAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}