package com.camo.auth_gateway.backendbridge.service;

import com.camo.auth_gateway.backendbridge.api.LoginAssertionConsumptionApi;
import com.camo.auth_gateway.backendbridge.api.exception.AssertionNotFoundException;
import com.camo.auth_gateway.backendbridge.api.exception.WrongAssertionTypeException;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionEntity;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionStatus;
import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionType;
import com.camo.auth_gateway.backendbridge.repository.BridgeAssertionRepository;
import com.camo.auth_gateway.backendbridge.web.dto.ConsumeLoginAssertionRequest;
import com.camo.auth_gateway.backendbridge.web.dto.ConsumeLoginAssertionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class LoginAssertionConsumptionService implements LoginAssertionConsumptionApi {
    private final BridgeAssertionRepository bridgeAssertionRepository;
    private final Clock clock;

    public LoginAssertionConsumptionService(BridgeAssertionRepository bridgeAssertionRepository, Clock clock, Clock clock1) {
        this.bridgeAssertionRepository = bridgeAssertionRepository;
        this.clock = clock1;
    }
    @Override
    @Transactional
    public ConsumeLoginAssertionResult consume(String jti, String clientId) {
        Instant now = Instant.now(clock);
        Optional<BridgeAssertionEntity> assertionOpt = bridgeAssertionRepository.findByJtiAndClientId(jti,clientId);
        if (assertionOpt.isEmpty()) {
            return new ConsumeLoginAssertionResult(
                    jti,
                    false,
                    ConsumeLoginAssertionReason.NOT_FOUND,
                    null
            );
        }
        BridgeAssertionEntity  assertion = assertionOpt.get();
        if (assertion.getAssertionType() != BridgeAssertionType.LOGIN) {
            return new ConsumeLoginAssertionResult(
                    jti,
                    false,
                    ConsumeLoginAssertionReason.ASSERTION_TYPE_MISMATCH,
                    null
            );
        }
        if (!assertion.getClientId().equals(clientId)) {
            return new ConsumeLoginAssertionResult(
                    jti,
                    false,
                    ConsumeLoginAssertionReason.CLIENT_MISMATCH,
                    null
            );
        }

        if (assertion.isExpired(now) || assertion.getStatus() == BridgeAssertionStatus.EXPIRED) {
            if (assertion.getStatus() != BridgeAssertionStatus.EXPIRED) {
                assertion.setExpired();
                bridgeAssertionRepository.save(assertion);
            }

            return new ConsumeLoginAssertionResult(
                    jti,
                    false,
                    ConsumeLoginAssertionReason.EXPIRED,
                    null
            );
        }
        assertion.consume(now);
        return new ConsumeLoginAssertionResult(
                jti,
                true,
                ConsumeLoginAssertionReason.CONSUMED,
                now
        );
    }
}
