package com.camo.auth_gateway.backendbridge.service;

import com.camo.auth_gateway.backendbridge.api.IssueLoginAssertionUseCase;
import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionCommand;
import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionResult;
import com.camo.auth_gateway.backendbridge.assertation.AssertionSigner;
import com.camo.auth_gateway.backendbridge.assertation.LoginAssertionFactory;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionEntity;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionStatus;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionType;
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
public class IssueLoginAssertionService implements IssueLoginAssertionUseCase {

    private static final long LOGIN_ASSERTION_TTL_SECONDS = 120;

    private final BridgeAssertionRepository bridgeAssertionRepository;
    private final LoginAssertionFactory loginAssertionClaimsFactory;
    private final AssertionSigner loginAssertionSigner;
    private final Clock clock;
    private final ClientSettingsLookupApi clientSettingsLookup;

    public IssueLoginAssertionService(BridgeAssertionRepository bridgeAssertionRepository,
                                      LoginAssertionFactory loginAssertionClaimsFactory,
                                      AssertionSigner loginAssertionSigner,
                                      Clock clock,ClientSettingsLookupApi clientSettingsLookup) {
        this.bridgeAssertionRepository = bridgeAssertionRepository;
        this.loginAssertionClaimsFactory = loginAssertionClaimsFactory;
        this.loginAssertionSigner = loginAssertionSigner;
        this.clock = clock;
        this.clientSettingsLookup = clientSettingsLookup;
    }
    @Override
    public IssueLoginAssertionResult createLoginAssertion(IssueLoginAssertionCommand command) {


        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(LOGIN_ASSERTION_TTL_SECONDS, ChronoUnit.SECONDS);

        BridgeAssertionEntity entity = BridgeAssertionEntity.builder()
                .jti(UUID.randomUUID().toString())
                .assertionType(BridgeAssertionType.LOGIN)
                .clientId(command.clientId())
                .subject(command.subject())
                .status(BridgeAssertionStatus.ISSUED)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .build();

        bridgeAssertionRepository.save(entity);
        ClientSettingsDto clientSettings = clientSettingsLookup.lookupClientSettingsWithClientId(command.clientId());
        JWTClaimsSet claims = loginAssertionClaimsFactory.create(clientSettings,entity, command.linkedAccountId());
        String signedAssertion = loginAssertionSigner.sign(claims);

        return new IssueLoginAssertionResult(
                entity.getJti(),
                entity.getExpiresAt(),
                signedAssertion
        );
    }
}
