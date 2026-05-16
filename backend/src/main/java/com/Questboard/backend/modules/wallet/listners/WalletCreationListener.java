package com.Questboard.backend.modules.wallet.listners;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.Questboard.backend.modules.auth.events.UserRegistrationEvent;
import com.Questboard.backend.modules.wallet.WalletRepository;
import com.Questboard.backend.modules.wallet.models.Wallet;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WalletCreationListener {

    private final WalletRepository walletRepository;

    public WalletCreationListener(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegistrationEvent event) {

        log.info("Creating wallet for: " + event.user().getEmail());

        Wallet wallet = new Wallet();
        wallet.setUser(event.user());

        walletRepository.save(wallet);

        log.info("Wallet created");
    }
}