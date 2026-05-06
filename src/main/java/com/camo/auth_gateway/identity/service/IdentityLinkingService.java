package com.camo.auth_gateway.identity.service;

import com.camo.auth_gateway.identity.api.IdentityLinkingApi;
import com.camo.auth_gateway.identity.api.model.PseudonymMappingDto;
import com.camo.auth_gateway.identity.domain.PseudonymMapping;
import com.camo.auth_gateway.identity.domain.PseudonymMappingStatus;
import com.camo.auth_gateway.identity.repository.PseudonymMappingRepository;
import org.springframework.transaction.annotation.Transactional;

public class IdentityLinkingService implements IdentityLinkingApi {

    private final PseudonymMappingRepository pseudonymMappingRepository;

    public IdentityLinkingService(PseudonymMappingRepository pseudonymMappingRepository) {
        this.pseudonymMappingRepository = pseudonymMappingRepository;
    }
    @Override
    @Transactional
    public PseudonymMappingDto createMapping(String clientId, String pseudonymValue, String externalUserId) {
        PseudonymMapping newMapping = new PseudonymMapping(clientId,pseudonymValue,externalUserId, null,  PseudonymMappingStatus.ACTIVE);
        return PseudonymMappingDto.from(newMapping);
    }
}
