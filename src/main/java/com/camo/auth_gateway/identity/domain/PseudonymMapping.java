package com.camo.auth_gateway.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        name = "pseudonym_mapping",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pseudonym_mapping_client_pseudonym",
                        columnNames = {"client_id", "pseudonym_value"}
                )
        }
)
public class PseudonymMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false, length = 100)
    private String clientId;

    @Column(name = "pseudonym_value", nullable = false, length = 255)
    private String pseudonymValue;

    @Column(name = "external_user_id", nullable = false, length = 255)
    private String externalUserId;

    @Column(name = "wallet_subject", length = 255)
    private String walletSubject;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PseudonymMappingStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected PseudonymMapping() {
    }

    public PseudonymMapping(
            String clientId,
            String pseudonymValue,
            String externalUserId,
            String walletSubject,
            PseudonymMappingStatus status
    ) {
        this.clientId = clientId;
        this.pseudonymValue = pseudonymValue;
        this.externalUserId = externalUserId;
        this.walletSubject = walletSubject;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = PseudonymMappingStatus.ACTIVE;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void markUsed() {
        this.lastUsedAt = Instant.now();
    }

    public void deactivate() {
        this.status = PseudonymMappingStatus.INACTIVE;
    }

    public void reactivate() {
        this.status = PseudonymMappingStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getPseudonymValue() {
        return pseudonymValue;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public String getWalletSubject() {
        return walletSubject;
    }

    public PseudonymMappingStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public void setWalletSubject(String walletSubject) {
        this.walletSubject = walletSubject;
    }

    public void setStatus(PseudonymMappingStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PseudonymMapping that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
