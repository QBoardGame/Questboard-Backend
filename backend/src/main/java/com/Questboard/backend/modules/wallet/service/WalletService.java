package com.Questboard.backend.modules.wallet.service;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Questboard.backend.modules.wallet.dto.WalletStatsDto;
import com.Questboard.backend.modules.wallet.dto.WalletTransactionPageResponse;
import com.Questboard.backend.modules.wallet.dto.WalletTransactionResponseDto;
import com.Questboard.backend.modules.wallet.dto.WalletUpdatedEvent;
import com.Questboard.backend.modules.wallet.enums.TransactionType;
import com.Questboard.backend.modules.wallet.enums.WalletCurrency;
import com.Questboard.backend.modules.wallet.listners.WalletUpdatePublisher;
import com.Questboard.backend.modules.wallet.models.Wallet;
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
        private final ApplicationEventPublisher eventPublisher;
        // private final WalletUpdatePublisher walletUpdatePublisher;

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
                        Long updated = walletRepository.addCoins(
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

                        eventPublisher.publishEvent(
                                        new WalletUpdatedEvent(userId, latestBalance));

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

        @Transactional(readOnly = true)
        public WalletStatsDto getWalletStats(UUID userId) {
                Wallet wallet = walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user " + userId));

                return WalletStatsDto.builder()
                                .cashBalance(wallet.getCashBalance())
                                .coinBalance(wallet.getCoinBalance())
                                .lockedCashBalance(wallet.getLockedCashBalance())
                                .lockedCoinBalance(wallet.getLockedCoinBalance())
                                .totalCashEarned(wallet.getTotalCashEarned())
                                .totalCoinsEarned(wallet.getTotalCoinsEarned())
                                .build();
        }

        // @Transactional(readOnly = true)
        // public Page<WalletTransaction> getWalletTransactions(UUID userId, Pageable
        // pageable) {
        // return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId,
        // pageable);
        // }
        @Transactional(readOnly = true)
        public WalletTransactionPageResponse getWalletTransactions(
                        UUID userId,
                        Pageable pageable) {

                Page<WalletTransaction> transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(
                                userId,
                                pageable);

                List<WalletTransactionResponseDto> content = transactions
                                .map(transaction -> WalletTransactionResponseDto.builder()
                                                .id(transaction.getId())
                                                .referenceId(transaction.getReferenceId())
                                                .currency(transaction.getCurrency())
                                                .type(transaction.getType())
                                                .cashAmount(transaction.getCashAmount())
                                                .coinAmount(transaction.getCoinAmount())
                                                .balanceAfterCoins(transaction.getBalanceAfterCoins())
                                                .description(transaction.getDescription())
                                                .createdAt(transaction.getCreatedAt()
                                                                .atZone(ZoneId.of("UTC"))
                                                                .toLocalDate())
                                                .build())
                                .getContent();

                return WalletTransactionPageResponse.builder()
                                .content(content)
                                .page(transactions.getNumber())
                                .size(transactions.getSize())
                                .totalElements(transactions.getTotalElements())
                                .totalPages(transactions.getTotalPages())
                                .first(transactions.isFirst())
                                .last(transactions.isLast())
                                .numberOfElements(transactions.getNumberOfElements())
                                .build();
        }
}
