package com.Questboard.backend.modules.wallet.listners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.Questboard.backend.modules.wallet.models.Wallet;

import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WalletListener {

    private static WalletUpdatePublisher publisher;

    @Autowired
    public void setPublisher(WalletUpdatePublisher publisher) {
        WalletListener.publisher = publisher;
    }

    @PostUpdate
    public void afterUpdate(Wallet wallet) {
        publisher.publish(wallet);
    }
}
