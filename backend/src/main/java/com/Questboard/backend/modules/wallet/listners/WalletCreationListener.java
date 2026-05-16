package com.Questboard.backend.modules.wallet.listners;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.Questboard.backend.modules.auth.events.UserRegistrationEvent;
import com.Questboard.backend.modules.wallet.WalletRepository;
import com.Questboard.backend.modules.wallet.models.Wallet;

@Component
public class WalletCreationListener {

    private final WalletRepository walletRepository;

    public WalletCreationListener(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegistrationEvent event) {

        Wallet wallet = new Wallet();
        wallet.setUser(event.user());
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);
    }
}