package com.camo.auth_gateway.backendbridge.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.bridge.assertion")
@NullMarked
public record BridgeAssertionProperties(
        String issuer,
        long registrationAssertionTtlSeconds,
        long loginAssertionTtlSeconds
) {
}