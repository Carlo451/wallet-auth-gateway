package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.wallet.domain.VerifiedClaims;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WalletCallbackService {

    private final WalletSessionRepository walletSessionRepository;

    @Transactional
    public void handleCallback(MultiValueMap<String, String> formData) {
        String state = formData.getFirst("state");
        String vpToken = formData.getFirst("vp_token");
        String error = formData.getFirst("error");

        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("Missing state");
        }

        WalletSession session = walletSessionRepository.findByState(state)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));

        if (session.isExpired()) {
            session.markExpired();
            walletSessionRepository.save(session);
            throw new IllegalStateException("Wallet session expired");
        }

        if (error != null && !error.isBlank()) {
            session.markFailed();
            walletSessionRepository.save(session);
            return;
        }

        if (vpToken == null || vpToken.isBlank()) {
            session.markFailed();
            walletSessionRepository.save(session);
            throw new IllegalArgumentException("Missing vp_token");
        }

        // TODO:
        // 1. vp_token kryptographisch prüfen
        // 2. nonce gegen session.getNonce() prüfen
        // 3. audience / client_id prüfen
        // 4. presentation_submission prüfen
        // 5. Claims aus vp_token extrahieren

        VerifiedClaims verifiedClaims = new VerifiedClaims(
                "demo-subject",
                "Max",
                "Mustermann",
                LocalDate.of(1999, 1, 1),
                true,
                "demo-issuer"
        );

        session.markVerified(verifiedClaims);
        walletSessionRepository.save(session);
    }
}
