package com.camo.auth_gateway.backendbridge.assertation;

import com.camo.auth_gateway.backendbridge.config.BridgeAssertionProperties;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionEntity;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.nimbusds.jwt.JWTClaimsSet;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Date;


@Component
@NullMarked
public class LoginAssertionFactory {
    private final BridgeAssertionProperties properties;

    public LoginAssertionFactory(BridgeAssertionProperties properties, Clock clock) {
        this.properties = properties;
    }

    public JWTClaimsSet create(ClientSettingsDto clientSettings, BridgeAssertionEntity entity, String externalUserId) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .issuer(properties.issuer())
                .audience(clientSettings.audience())
                .issueTime(Date.from(entity.getIssuedAt()))
                .expirationTime(Date.from(entity.getExpiresAt()))
                .jwtID(entity.getJti())
                .claim("client_id", entity.getClientId())
                .claim("flow_type", "login")
                .claim("linked_account_id", externalUserId);

        if (!entity.getSubject().isBlank()) {
            builder.subject(entity.getSubject());
        }

        return builder.build();
    }
}
