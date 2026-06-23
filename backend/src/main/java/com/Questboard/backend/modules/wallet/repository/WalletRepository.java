package com.Questboard.backend.modules.wallet.repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.Questboard.backend.modules.wallet.models.Wallet;

import jakarta.transaction.Transactional;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("""
            UPDATE Wallet w
            SET w.coinBalance = w.coinBalance + :amount,
                w.totalCoinsEarned = w.totalCoinsEarned + :amount
            WHERE w.user.id = :userId
            """)
    Long addCoins(UUID userId, Long amount);

    @Modifying
    @Query("""
            UPDATE Wallet w
            SET w.cashBalance = w.cashBalance + :amount,
                w.totalCashEarned = w.totalCashEarned + :amount
            WHERE w.user.id = :userId
            """)
    int addCash(UUID userId, BigDecimal amount);

    Optional<Wallet> findByUserId(UUID userId);

    @Query("""
            SELECT w.coinBalance
            FROM Wallet w
            WHERE w.user.id = :userId
            """)
    Long getCoinBalance(UUID userId);

    @Query("""
            SELECT w.cashBalance
            FROM Wallet w
            WHERE w.user.id = :userId
            """)
    BigDecimal getCashBalance(UUID userId);
}
