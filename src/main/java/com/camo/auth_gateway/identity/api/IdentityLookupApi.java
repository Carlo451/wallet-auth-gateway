package com.camo.auth_gateway.identity.api;

import com.camo.auth_gateway.identity.api.model.PseudonymMappingDto;

import java.util.Optional;

public interface IdentityLookupApi {
    Optional<PseudonymMappingDto> findActiveMapping(String clientId, String pseudonymValue);
    boolean checkIfPseudonymMappingExists(String clientId, String pseudonymValue);
}
