package com.camo.auth_gateway.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "registration_session",
        indexes = {
                @Index(name = "idx_registration_session_session_id", columnList = "session_id", unique = true),
                @Index(name = "idx_registration_session_client_id", columnList = "client_id"),
                @Index(name = "idx_registration_session_expires_at", columnList = "expires_at")
        }
)
public class RegistrationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, updatable = false, length = 36, unique = true)
    private String sessionId;

    @Column(name = "client_id", nullable = false, length = 100)
    private String clientId;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "nonce", length = 100)
    private String nonce;

    @Column(name = "pseudonym_value", length = 255)
    private String pseudonymValue;

    @Column(name = "wallet_subject", length = 255)
    private String walletSubject;

    @Column(name = "external_user_id", length = 255)
    private String externalUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private RegistrationSessionStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected RegistrationSession() {
    }

    public RegistrationSession(
            String clientId,
            String state,
            String nonce,
            Instant expiresAt
    ) {
        this.clientId = clientId;
        this.state = state;
        this.nonce = nonce;
        this.expiresAt = expiresAt;
        this.status = RegistrationSessionStatus.CREATED;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.sessionId == null) {
            this.sessionId = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = RegistrationSessionStatus.CREATED;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void markWalletVerified(String pseudonymValue, String walletSubject) {
        this.pseudonymValue = pseudonymValue;
        this.walletSubject = walletSubject;
        this.status = RegistrationSessionStatus.WALLET_VERIFIED;
    }

    public void markLinked(String externalUserId) {
        this.externalUserId = externalUserId;
        this.completedAt = Instant.now();
        this.status = RegistrationSessionStatus.LINKED;
    }

    public void markCancelled() {
        this.cancelledAt = Instant.now();
        this.status = RegistrationSessionStatus.CANCELLED;
    }

    public void markExpired() {
        this.status = RegistrationSessionStatus.EXPIRED;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public Long getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getState() {
        return state;
    }

    public String getNonce() {
        return nonce;
    }

    public String getPseudonymValue() {
        return pseudonymValue;
    }

    public String getWalletSubject() {
        return walletSubject;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public RegistrationSessionStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistrationSession that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}