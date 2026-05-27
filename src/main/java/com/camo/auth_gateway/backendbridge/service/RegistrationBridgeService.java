package com.camo.auth_gateway.backendbridge.service;

import com.camo.auth_gateway.backendbridge.api.RegistrationBridgeApi;
import com.camo.auth_gateway.backendbridge.api.dto.BackendRegistrationFailedException;
import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeCommand;
import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeResult;
import com.camo.auth_gateway.backendbridge.assertation.AssertionSigner;
import com.camo.auth_gateway.backendbridge.assertation.RegistrationAssertionFactory;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionEntity;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionStatus;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionType;
import com.camo.auth_gateway.backendbridge.http.RegistrationBackendClient;
import com.camo.auth_gateway.backendbridge.http.dto.RegistrationAssertionRequest;
import com.camo.auth_gateway.backendbridge.http.dto.RegistrationBackendResponse;
import com.camo.auth_gateway.backendbridge.repository.BridgeAssertionRepository;
import com.camo.auth_gateway.settings.api.ClientSettingsLookupApi;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RegistrationBridgeService implements RegistrationBridgeApi {
    private final ClientSettingsLookupApi clientSettingsLookupApi;
    private final RegistrationBackendClient registrationBackendClient;
    private final RegistrationAssertionFactory registrationAssertionFactory;
    private final AssertionSigner assertionSigner;
    private final Clock clock;
    private final BridgeAssertionRepository bridgeAssertionRepository;

    private final long REGISTER_ASSERTION_TTL_SECONDS = 300;

    public RegistrationBridgeService(ClientSettingsLookupApi clientSettingsLookupApi, RegistrationBackendClient registrationBackendClient,
                                     RegistrationAssertionFactory registrationAssertionFactory, AssertionSigner assertionSigner, Clock clock, BridgeAssertionRepository bridgeAssertionRepository) {
        this.clientSettingsLookupApi = clientSettingsLookupApi;
        this.registrationBackendClient = registrationBackendClient;
        this.registrationAssertionFactory = registrationAssertionFactory;
        this.assertionSigner = assertionSigner;
        this.clock = clock;
        this.bridgeAssertionRepository = bridgeAssertionRepository;
    }
    @Override
    public RegistrationBridgeResult register(RegistrationBridgeCommand command) throws BackendRegistrationFailedException {
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(REGISTER_ASSERTION_TTL_SECONDS, ChronoUnit.SECONDS);

        BridgeAssertionEntity entity = BridgeAssertionEntity.builder()
                .jti(command.jti())
                .assertionType(BridgeAssertionType.REGISTRATION)
                .clientId(command.clientId())
                .subject("Register")
                .status(BridgeAssertionStatus.ISSUED)
                .issuedAt(now)
                .expiresAt(expiresAt)

                .build();

        bridgeAssertionRepository.save(entity);
        ClientSettingsDto clientSettings = clientSettingsLookupApi.lookupClientSettingsWithClientId(command.clientId());
        JWTClaimsSet claimsSet = registrationAssertionFactory.create(clientSettings,command);
        RegistrationAssertionRequest registrationAssertionRequest = new RegistrationAssertionRequest(assertionSigner.sign(claimsSet));
        RegistrationBackendResponse registerClientResponse = registrationBackendClient.register(clientSettings.baseUrl()+clientSettings.registrationEndpoint(),registrationAssertionRequest);
        return new RegistrationBridgeResult(registerClientResponse.externalUserId());
    }
}
