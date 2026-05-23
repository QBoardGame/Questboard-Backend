package com.Questboard.backend.modules.wallet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Questboard.backend.modules.wallet.models.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID>{
    
}
