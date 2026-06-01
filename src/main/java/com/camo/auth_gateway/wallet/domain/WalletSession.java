package com.camo.auth_gateway.wallet.domain;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "client_id")
    private String clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_state", nullable = false, length = 30)
    private WalletFlowState flowState;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_type", nullable = false, length = 30)
    private WalletFlowType flowType;

    @Column(name = "external_verification_id")
    private String externalVerificationId;

    @Column(name = "frontend_redirect_uri")
    private String frontendRedirectUri;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "verified_claims", columnDefinition = "jsonb")
    private VerifiedClaims verifiedClaims;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "linked_identity_id")
    private String linkedIdentityId;

    @Lob
    private String encEcJwkJson;

    protected WalletSession() {
    }

    public WalletSession(UUID id,
                         String state,
                         String nonce,
                         WalletFlowState flowState,
                         String frontendRedirectUri,
                         Instant createdAt,
                         Instant expiresAt,
                         WalletFlowType flowType,
                         String clientId) {
        this.id = id;
        this.state = state;
        this.nonce = nonce;
        this.flowState = flowState;
        this.frontendRedirectUri = frontendRedirectUri;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.flowType = flowType;
        this.clientId = clientId;
    }

    public static WalletSession createNew(String frontendRedirectUri, Instant expiresAt,WalletFlowType flowType, String clientId) {
        Instant now = Instant.now();

        return new WalletSession(
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                WalletFlowState.CREATED,
                frontendRedirectUri,
                now,
                expiresAt,
                flowType,
                clientId
        );
    }

    public void markPending() {
        this.flowState = WalletFlowState.PENDING;
    }

    public void markVerified(VerifiedClaims verifiedClaims, String linkedIdentityId) {
        this.flowState = WalletFlowState.VERIFIED;
        this.verifiedClaims = verifiedClaims;
        this.verifiedAt = Instant.now();
        this.linkedIdentityId = linkedIdentityId;

    }

    public void markError(Exception error) {
        this.flowState = WalletFlowState.FAILED;
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

    public void setEncEcJwkJson(String encEcJwkJson) {
        this.encEcJwkJson = encEcJwkJson;
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

    public String getClientId() {
        return clientId;
    }

    public WalletFlowType getFlowType() {
        return flowType;
    }

    public String getLinkedIdentityId() {
        return linkedIdentityId;
    }

    public String getEncEcJwkJson() {
        return encEcJwkJson;
    }
}