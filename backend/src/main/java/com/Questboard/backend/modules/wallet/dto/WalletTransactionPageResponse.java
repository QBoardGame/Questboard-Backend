package com.Questboard.backend.modules.wallet.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletTransactionPageResponse {

    private final List<WalletTransactionResponseDto> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final int numberOfElements;
}
