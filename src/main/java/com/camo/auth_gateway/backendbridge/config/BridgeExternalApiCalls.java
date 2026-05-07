package com.camo.auth_gateway.backendbridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.bridge.external.api-calls")
public record BridgeExternalApiCalls(
        long registerReadTimoutSeconds,
        long loginReadTimoutSeconds

) {

}
