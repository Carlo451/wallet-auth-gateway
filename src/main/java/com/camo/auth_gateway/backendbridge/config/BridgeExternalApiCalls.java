package com.camo.auth_gateway.backendbridge.config;

import jakarta.validation.constraints.Null;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.bridge.external.api-calls")
@NullMarked
public record BridgeExternalApiCalls(
        long registerReadTimoutSeconds,
        long loginReadTimoutSeconds

) {

}
