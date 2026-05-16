package com.Questboard.backend.modules.wallet;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Questboard.backend.modules.wallet.models.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    
}
