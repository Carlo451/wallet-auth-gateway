package com.camo.auth_gateway.wallet.service;


import com.camo.auth_gateway.backendbridge.api.IssueLoginAssertionUseCase;
import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionCommand;
import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionResult;
import com.camo.auth_gateway.backendbridge.assertation.LoginAssertionFactory;
import com.camo.auth_gateway.identity.api.IdentityCreationApi;
import com.camo.auth_gateway.identity.api.IdentityLookupApi;
import com.camo.auth_gateway.identity.api.model.PseudonymMappingDto;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.repository.ClientSettingRepository;
import com.camo.auth_gateway.wallet.domain.VerifiedClaims;
import com.camo.auth_gateway.wallet.domain.VerifiedDisclosure;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.heidi.DecodedDisclosure;
import com.camo.auth_gateway.wallet.dto.heidi.HeidiVPTokenObject;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import com.camo.auth_gateway.walletprovs.HeidiWallet;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WalletCallbackService {

    private final WalletSessionRepository walletSessionRepository;
    private final IdentityLookupApi identityLookupApi;
    private final ObjectMapper objectMapper;
    private final IdentityCreationApi identityCreationApi;

    @Transactional
    public void handleCallback(MultiValueMap<String, String> formData) throws Exception {
        String state = formData.getFirst("state");
        String vpToken = formData.getFirst("vp_token");
        String error = formData.getFirst("error");

        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("Missing state");
        }

        WalletSession session = walletSessionRepository.findByState(state)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));

        if (session.isExpired()) {
            session.markExpired();
            walletSessionRepository.save(session);
            throw new IllegalStateException("Wallet session expired");
        }

        if (error != null && !error.isBlank()) {
            session.markFailed();
            walletSessionRepository.save(session);
            return;
        }

        if (vpToken == null || vpToken.isBlank()) {
            session.markFailed();
            walletSessionRepository.save(session);
            throw new IllegalArgumentException("Missing vp_token");
        }

        HeidiVPTokenObject heidiObj = HeidiVPTokenObject.parseVpToken(vpToken,objectMapper);
        if (!heidiObj.verifiyDisclosures() || !heidiObj.verifyKeyBinding()) {
            session.markFailed();
            walletSessionRepository.save(session);
            return;
        }


        HeidiWallet wallet = new HeidiWallet(objectMapper,identityCreationApi,"");
        String identification = wallet.buildWalletIdFromWalletDisclosures(heidiObj);



        Optional<PseudonymMappingDto> identDto = identityLookupApi.findActiveMapping(session.getClientId(), identification);

        VerifiedClaims verifiedClaims = new VerifiedClaims();
        for (DecodedDisclosure disclosure: heidiObj.getDecodedDisclosureList()) {
            VerifiedDisclosure disc = new VerifiedDisclosure();
            disc.setClaimName(disclosure.claimName());
            disc.setClaimValue(disclosure.claimValue());
            disc.setDigest(disclosure.digest());
            disc.setSalt(disclosure.salt());
            verifiedClaims.getClaims().add(disc);
        }
        if (session.getFlowType().equals(WalletFlowType.LOGIN) ) {
            if (identDto.isEmpty()) {
                session.markError(new IllegalArgumentException("Could not map identwith db."));
            } else {
                session.setExternalVerificationId(identDto.get().externalUserId());
                session.markVerified(verifiedClaims,identification);
            }
        } else if (WalletFlowType.REGISTRATION.equals(session.getFlowType())) {
            if (identDto.isPresent()) {
                session.markError(new IllegalArgumentException("Ident exists in db"));
            } else {
                session.markVerified(verifiedClaims,identification);
            }
        }
        walletSessionRepository.save(session);
    }


}
