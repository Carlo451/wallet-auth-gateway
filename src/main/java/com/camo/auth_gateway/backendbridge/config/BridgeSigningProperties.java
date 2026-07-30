package com.camo.auth_gateway.backendbridge.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "gateway.bridge.signing")
@NullMarked
public record BridgeSigningProperties(
        String keyId,
        String type,
        Resource walletSignPrivKey
) {
}
