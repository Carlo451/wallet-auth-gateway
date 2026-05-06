package com.camo.auth_gateway.wallet.domain;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallet_session")
public class WalletSession {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "state", nullable = false, unique = true, length = 100)
    private String state;

    @Column(name = "nonce", nullable = false, unique = true, length = 100)
    private String nonce;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_state", nullable = false, length = 30)
    private WalletFlowState flowState;

    @Column(name = "external_verification_id")
    private String externalVerificationId;

    @Column(name = "frontend_redirect_uri")
    private String frontendRedirectUri;

    @Embedded
    private VerifiedClaims verifiedClaims;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    protected WalletSession() {
    }

    public WalletSession(UUID id,
                         String state,
                         String nonce,
                         WalletFlowState flowState,
                         String frontendRedirectUri,
                         Instant createdAt,
                         Instant expiresAt) {
        this.id = id;
        this.state = state;
        this.nonce = nonce;
        this.flowState = flowState;
        this.frontendRedirectUri = frontendRedirectUri;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public static WalletSession createNew(String frontendRedirectUri, Instant expiresAt) {
        Instant now = Instant.now();

        return new WalletSession(
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                WalletFlowState.CREATED,
                frontendRedirectUri,
                now,
                expiresAt
        );
    }

    public void markPending() {
        this.flowState = WalletFlowState.PENDING;
    }

    public void markVerified(VerifiedClaims verifiedClaims) {
        this.flowState = WalletFlowState.VERIFIED;
        this.verifiedClaims = verifiedClaims;
        this.verifiedAt = Instant.now();
    }

    public void markFailed() {
        this.flowState = WalletFlowState.FAILED;
    }

    public void markExpired() {
        this.flowState = WalletFlowState.EXPIRED;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public UUID getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public String getNonce() {
        return nonce;
    }

    public WalletFlowState getFlowState() {
        return flowState;
    }

    public String getExternalVerificationId() {
        return externalVerificationId;
    }

    public void setExternalVerificationId(String externalVerificationId) {
        this.externalVerificationId = externalVerificationId;
    }

    public String getFrontendRedirectUri() {
        return frontendRedirectUri;
    }

    public VerifiedClaims getVerifiedClaims() {
        return verifiedClaims;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }
}