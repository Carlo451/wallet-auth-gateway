package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.backendbridge.api.IssueLoginAssertionUseCase;
import com.camo.auth_gateway.backendbridge.api.RegistrationBridgeApi;
import com.camo.auth_gateway.backendbridge.api.dto.*;
import com.camo.auth_gateway.identity.api.IdentityLinkingApi;
import com.camo.auth_gateway.identity.api.model.PseudonymMappingDto;
import com.camo.auth_gateway.identity.api.model.RegistrationSessionDto;
import com.camo.auth_gateway.identity.domain.RegistrationSessionStatus;
import com.camo.auth_gateway.identity.service.RegistrationService;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.repository.ClientSettingRepository;
import com.camo.auth_gateway.wallet.domain.VerifiedDisclosure;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.WalletAuthContinueResponse;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WalletContinueService {

    private final IssueLoginAssertionUseCase issueLoginAssertionUseCase;
    private final ClientSettingRepository clientSettingRepository;
    private final RegistrationBridgeApi  registrationBridgeApi;
    private final RegistrationService registrationService;
    private final IdentityLinkingApi identityLinkingApi;

    public WalletAuthContinueResponse createLoginAssertion(WalletSession session) {
        ClientSettingsDto dto = ClientSettingsDto.from(clientSettingRepository.findByClientId(session.getClientId()).orElseThrow(() -> new EntityNotFoundException("Could not find ClientId")));
        IssueLoginAssertionCommand cpmmand = new IssueLoginAssertionCommand(session.getClientId(), WalletFlowType.LOGIN.toString(), session.getExternalVerificationId());
        return new WalletAuthContinueResponse(issueLoginAssertionUseCase.createLoginAssertion(cpmmand),dto.baseUrl()+dto.loginEndpoint());
    }

    public WalletAuthContinueResponse startRegistration(WalletSession session) {
        ClientSettingsDto dto = ClientSettingsDto.from(clientSettingRepository.findByClientId(session.getClientId()).orElseThrow(() -> new EntityNotFoundException("Could not find ClientId")));
        List<String> requestdClaims = dto.requestedClaimsForRegister();
        List<VerifiedDisclosure> disclosures =  session.getVerifiedClaims().getClaims();
        Map<String,Object> claimsForBackend = new HashMap<>();
        for(String claim : requestdClaims){
            for (VerifiedDisclosure  disclosure:  disclosures) {
                if (disclosure.getClaimName().equals(claim)) {
                    claimsForBackend.put(claim, disclosure.getClaimValue());
                }
            }
        }
        Instant expires = Instant.now().plusSeconds(300);
        String sessionId = registrationService.createSession(session.getClientId(), RegistrationSessionStatus.CREATED.toString(), UUID.randomUUID().toString(),expires);
        RegistrationBridgeCommand command = new RegistrationBridgeCommand(UUID.randomUUID().toString(),session.getClientId(),sessionId,session.getLinkedIdentityId(),claimsForBackend);
        try {
            RegistrationBridgeResult registerResult = registrationBridgeApi.register(command);
            PseudonymMappingDto mapping =  identityLinkingApi.createMapping(session.getClientId(),session.getLinkedIdentityId(), registerResult.externalUserId());
            registrationService.completeRegistration(sessionId,registerResult.externalUserId());
        } catch (BackendRegistrationFailedException exc) {

        }

        return null;
    }
}
