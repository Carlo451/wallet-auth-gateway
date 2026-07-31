package com.camo.auth_gateway.backendbridge.assertation;

import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeCommand;
import com.camo.auth_gateway.backendbridge.config.BridgeAssertionProperties;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.domain.ClientSettings;
import com.nimbusds.jwt.JWTClaimsSet;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
@NullMarked
public class RegistrationAssertionFactory {

    private final Clock clock;
    private final BridgeAssertionProperties properties;

    public RegistrationAssertionFactory(Clock clock, BridgeAssertionProperties properties) {
        this.clock = clock;
        this.properties = properties;
    }

    public JWTClaimsSet create(ClientSettingsDto clientSettings, RegistrationBridgeCommand command) {
        Instant issuedAt = Instant.now(clock);
        Instant expiresAt = issuedAt.plusSeconds(properties.registrationAssertionTtlSeconds());

        Map<String, Object> verifiedClaims = new LinkedHashMap<>(command.verifiedClaims());

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .issuer(properties.issuer())
                .audience(clientSettings.audience())
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .jwtID(command.jti())
                .claim("client_id", clientSettings.clientId())
                .claim("flow_type", "registration")
                .claim("pseudonym_value", command.pseudonymValue())
                .claim("verified_claims", verifiedClaims);

        return builder.build();
    }
}
