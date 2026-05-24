package com.Questboard.backend.modules.wallet.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Questboard.backend.modules.auth.dto.JwtUserPrincipal;
import com.Questboard.backend.modules.wallet.dto.WalletStatsDto;
import com.Questboard.backend.modules.wallet.dto.WalletTransactionPageResponse;
import com.Questboard.backend.modules.wallet.dto.WalletTransactionResponseDto;
import com.Questboard.backend.modules.wallet.models.WalletTransaction;
import com.Questboard.backend.modules.wallet.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/stats")
    public ResponseEntity<WalletStatsDto> getWalletStats(
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        WalletStatsDto stats = walletService.getWalletStats(principal.getUserId());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/transactions")
    public ResponseEntity<WalletTransactionPageResponse> getWalletTransactions(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var transactions = walletService.getWalletTransactions(principal.getUserId(), pageable);

        List<WalletTransactionResponseDto> content = transactions.map(transaction -> WalletTransactionResponseDto.builder()
                .id(transaction.getId())
                .referenceId(transaction.getReferenceId())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .cashAmount(transaction.getCashAmount())
                .coinAmount(transaction.getCoinAmount())
                .balanceAfterCoins(transaction.getBalanceAfterCoins())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build())
                .getContent();

        WalletTransactionPageResponse response = WalletTransactionPageResponse.builder()
                .content(content)
                .page(transactions.getNumber())
                .size(transactions.getSize())
                .totalElements(transactions.getTotalElements())
                .totalPages(transactions.getTotalPages())
                .first(transactions.isFirst())
                .last(transactions.isLast())
                .numberOfElements(transactions.getNumberOfElements())
                .build();

        return ResponseEntity.ok(response);
    }
}
