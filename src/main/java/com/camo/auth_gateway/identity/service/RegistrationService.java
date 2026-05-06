package com.camo.auth_gateway.identity.service;

import com.camo.auth_gateway.identity.api.RegistrationApi;
import com.camo.auth_gateway.identity.domain.RegistrationSession;
import com.camo.auth_gateway.identity.repository.RegistrationSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class RegistrationService implements RegistrationApi {

    private final RegistrationSessionRepository registrationSessionRepository;

    public RegistrationService(RegistrationSessionRepository registrationSessionRepository) {
        this.registrationSessionRepository = registrationSessionRepository;
    }

    @Override
    public String createSession(String clientId, String state, String nonce, Instant expiresAt) {
        RegistrationSession session = new RegistrationSession(clientId, state, nonce, expiresAt);
        registrationSessionRepository.save(session);
        return session.getSessionId();
    }

    @Override
    public void markWalletVerified(String sessionId, String pseudonymValue, String walletSubject) {
        RegistrationSession session = registrationSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Registration session not found"));

        session.markWalletVerified(pseudonymValue, walletSubject);
    }

    @Override
    public void completeRegistration(String sessionId, String externalUserId) {
        RegistrationSession session = registrationSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Registration session not found"));

        session.markLinked(externalUserId);
    }
}