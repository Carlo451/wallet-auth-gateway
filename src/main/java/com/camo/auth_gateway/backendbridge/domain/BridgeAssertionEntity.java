package com.camo.auth_gateway.backendbridge.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

import java.time.Instant;

@Entity
@Table(name = "bridge_assertion")
public class BridgeAssertionEntity {

    @Id
    @Column(name = "jti", nullable = false, updatable = false, length = 100)
    private String jti;

    @Column(name = "client_id", nullable = false, length = 100)
    private String clientId;


    @Column(name = "subject", length = 255)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "assertion_type", nullable = false, length = 30)
    private BridgeAssertionType assertionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BridgeAssertionStatus status;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    protected BridgeAssertionEntity() {
    }
    @Builder
    public BridgeAssertionEntity(String jti,
                                 String clientId,

                                 String subject,
                                 BridgeAssertionType assertionType,
                                 BridgeAssertionStatus status,
                                 Instant issuedAt,
                                 Instant expiresAt) {
        this.jti = jti;
        this.clientId = clientId;

        this.subject = subject;
        this.assertionType = assertionType;
        this.status = status;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public void consume(Instant consumedAt) {
        this.status = BridgeAssertionStatus.CONSUMED;
        this.consumedAt = consumedAt;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }

    public void setExpired() {
        this.status = BridgeAssertionStatus.EXPIRED;
    }

    public boolean isConsumable(Instant now) {
        return status == BridgeAssertionStatus.ISSUED && !isExpired(now);
    }

    public String getJti() {
        return jti;
    }

    public String getClientId() {
        return clientId;
    }


    public String getSubject() {
        return subject;
    }

    public BridgeAssertionType getAssertionType() {
        return assertionType;
    }

    public BridgeAssertionStatus getStatus() {
        return status;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getConsumedAt() {
        return consumedAt;
    }
}
