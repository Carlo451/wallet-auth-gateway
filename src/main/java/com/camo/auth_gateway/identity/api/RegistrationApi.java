package com.camo.auth_gateway.identity.api;

import java.time.Instant;

public interface RegistrationApi {
    String createSession(String clientId, String state, String nonce, Instant expiresAt);
    void markWalletVerified(String sessionId, String pseudonymValue, String walletSubject);
    void completeRegistration(String sessionId, String externalUserId);
}