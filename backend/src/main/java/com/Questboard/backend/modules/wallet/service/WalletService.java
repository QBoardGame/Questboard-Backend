package com.Questboard.backend.modules.wallet.service;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.Questboard.backend.modules.wallet.enums.TransactionType;
import com.Questboard.backend.modules.wallet.enums.WalletCurrency;
import com.Questboard.backend.modules.wallet.models.WalletTransaction;
import com.Questboard.backend.modules.wallet.repository.WalletRepository;
import com.Questboard.backend.modules.wallet.repository.WalletTransactionRepository;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean creditCoins(
            UUID userId,
            Long amount,
            TransactionType type,
            UUID referenceId,
            String description) {

        try {

            // -----------------------------------
            // 1. Atomic Balance Increment
            // -----------------------------------
            int updated = walletRepository.addCoins(
                    userId,
                    amount);

            // -----------------------------------
            // 2. Wallet Not Found
            // -----------------------------------
            if (updated == 0) {

                log.warn(
                        "Wallet not found while crediting coins. userId={}, amount={}, type={}, referenceId={}",
                        userId,
                        amount,
                        type,
                        referenceId);

                return false;
            }
            walletRepository.flush();

            // -----------------------------------
            // 3. Fetch Updated Balance
            // -----------------------------------
            Long latestBalance = walletRepository.getCoinBalance(userId);

            // -----------------------------------
            // 4. Create Wallet Transaction
            // -----------------------------------
            WalletTransaction transaction = WalletTransaction.builder()
                    .userId(userId)
                    .currency(WalletCurrency.COINS)
                    .type(type)
                    .coinAmount(amount)
                    .balanceAfterCoins(latestBalance)
                    .referenceId(referenceId)
                    .description(description)
                    .build();

            transactionRepository.save(transaction);

            // -----------------------------------
            // 5. Success Log
            // -----------------------------------
            log.info(
                    "Coins credited successfully. userId={}, amount={}, latestBalance={}, type={}, referenceId={}",
                    userId,
                    amount,
                    latestBalance,
                    type,
                    referenceId);

            return true;

        } catch (DataIntegrityViolationException e) {

            // -----------------------------------
            // Duplicate Reward Protection
            // -----------------------------------
            log.warn(
                    "Duplicate wallet transaction ignored. userId={}, referenceId={}, type={}",
                    userId,
                    referenceId,
                    type);

            return false;

        } catch (Exception e) {

            // -----------------------------------
            // General Failure
            // -----------------------------------
            log.error(
                    "Error crediting coins. userId={}, amount={}, type={}, referenceId={}",
                    userId,
                    amount,
                    type,
                    referenceId,
                    e);

            throw new RuntimeException("Wallet credit failed for user " + userId);
        }
    }
}