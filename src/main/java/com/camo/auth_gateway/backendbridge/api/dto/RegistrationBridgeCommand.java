package com.camo.auth_gateway.backendbridge.api.dto;

import java.util.Map;

public record RegistrationBridgeCommand(
        String jti,
        String clientId,
        String pseudonymValue,
        Map<String, Object> verifiedClaims
) {
}
