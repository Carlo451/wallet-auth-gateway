package com.camo.auth_gateway.identity.api;

import com.camo.auth_gateway.identity.api.model.PseudonymMappingDto;

public interface IdentityLinkingApi {
    PseudonymMappingDto createMapping(String clientId, String pseudonymValue, String externalUserId);
}
