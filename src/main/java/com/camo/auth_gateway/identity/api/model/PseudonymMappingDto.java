package com.camo.auth_gateway.identity.api.model;

import com.camo.auth_gateway.identity.domain.PseudonymMapping;

import java.time.Instant;

public record PseudonymMappingDto(
        String clientId,
        String pseudonymValue,
        String externalUserId,
        Instant createdAt,
        Instant lastUsedAt

) {
    public static PseudonymMappingDto from(PseudonymMapping mapping) {
        return new PseudonymMappingDto(
                mapping.getClientId(),
                mapping.getPseudonymValue(),
                mapping.getExternalUserId(),
                mapping.getCreatedAt(),
                mapping.getLastUsedAt()
        );
    }
}
