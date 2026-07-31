package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.backendbridge.api.IssueLoginAssertionUseCase;
import com.camo.auth_gateway.backendbridge.api.RegistrationBridgeApi;
import com.camo.auth_gateway.backendbridge.api.dto.*;
import com.camo.auth_gateway.identity.api.IdentityLinkingApi;
import com.camo.auth_gateway.identity.api.model.PseudonymMappingDto;
import com.camo.auth_gateway.identity.domain.RegistrationSessionStatus;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.repository.ClientSettingRepository;
import com.camo.auth_gateway.wallet.domain.VerifiedDisclosure;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.WalletAuthContinueResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WalletContinueService {

    private final IssueLoginAssertionUseCase issueLoginAssertionUseCase;
    private final ClientSettingRepository clientSettingRepository;
    private final RegistrationBridgeApi  registrationBridgeApi;
    private final IdentityLinkingApi identityLinkingApi;


    public WalletAuthContinueResponse createLoginAssertion(WalletSession session) throws IllegalArgumentException {
        ClientSettingsDto dto = ClientSettingsDto.from(clientSettingRepository.findByClientId(session.getClientId()).orElseThrow(() -> new IllegalArgumentException("Could not find ClientId")));
        IssueLoginAssertionCommand command = new IssueLoginAssertionCommand(session.getClientId(), WalletFlowType.LOGIN.toString(), session.getExternalVerificationId());
        IssueLoginAssertionResult assertionResult = issueLoginAssertionUseCase.createLoginAssertion(command);
        String loginEndpoint = UriComponentsBuilder.fromPath(dto.baseUrl())
                .path(dto.loginEndpoint())
                .path(assertionResult.signedAssertion())
                .toUriString();
        return new WalletAuthContinueResponse(assertionResult,loginEndpoint);
    }

    public boolean startRegistration(WalletSession session) {
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
        RegistrationBridgeCommand command = new RegistrationBridgeCommand(UUID.randomUUID().toString(),session.getClientId(),session.getLinkedIdentityId(),claimsForBackend);
        try {
            RegistrationBridgeResult registerResult = registrationBridgeApi.register(command);
            identityLinkingApi.createMapping(session.getClientId(),session.getLinkedIdentityId(), registerResult.externalUserId());
            return true;
        } catch (BackendRegistrationFailedException exc) {
            throw exc;
        }
    }
}
