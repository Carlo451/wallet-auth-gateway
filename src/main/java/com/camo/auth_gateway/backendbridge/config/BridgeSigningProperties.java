package com.camo.auth_gateway.backendbridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.bridge.signing")
public record BridgeSigningProperties(
        String keyId,
        String type
) {
}
