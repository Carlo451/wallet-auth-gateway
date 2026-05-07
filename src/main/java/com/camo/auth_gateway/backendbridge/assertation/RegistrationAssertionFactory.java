package com.camo.auth_gateway.backendbridge.assertation;

import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeCommand;
import com.camo.auth_gateway.backendbridge.config.BridgeAssertionProperties;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.domain.ClientSettings;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
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
                .jwtID(UUID.randomUUID().toString())
                .claim("client_id", clientSettings.clientId())
                .claim("flow_type", "registration")
                .claim("registration_session_id", command.registrationSessionId())
                .claim("pseudonym_value", command.pseudonymValue())
                .claim("verified_claims", verifiedClaims);

        if (command.walletSubject() != null && !command.walletSubject().isBlank()) {
            builder.claim("wallet_subject", command.walletSubject());
        }

        return builder.build();
    }
}
