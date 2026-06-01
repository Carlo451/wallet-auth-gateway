package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.common.config.keys.ECKeyProvider;
import com.camo.auth_gateway.common.config.keys.SigningKeys;
import com.camo.auth_gateway.settings.api.ClientSettingsLookupApi;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.service.SettingsLookupService;
import com.camo.auth_gateway.wallet.domain.WalletFlowState;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.dto.WalletRegSuccessResponse;
import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import com.camo.auth_gateway.walletprovs.HeidiWallet;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WalletFlowService {

    @Value("${gateway.app.network.baseurl}")
    private String baseUrl;

    private final WalletSessionRepository walletSessionRepository;
    private final SigningKeys signingKeys;
    private final ObjectMapper objectMapper;
    private final ECKeyProvider keyProv;
    private final ClientSettingsLookupApi clientSettingsLookupApi;

    public StartWalletLoginResponse startLogin(StartWalletLoginRequest request) {
        Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);

        WalletSession session = WalletSession.createNew(
                request.redirectUri(),
                expiresAt,
                request.flowType(),
                request.clientId()

        );

        session.markPending();
        walletSessionRepository.save(session);

        String requestUri = baseUrl+"/api/wallet/request/" + session.getId();
        String openid4vpUrl = "openid4vp://authorize?request_uri_method=post&request_uri=" + requestUri;

        return new StartWalletLoginResponse(
                session.getId(),
                session.getFlowState().name(),
                requestUri,
                openid4vpUrl,
                session.getExpiresAt()
        );
    }

    @Transactional
    public String getRequestObject(UUID sessionId) throws Exception {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));
        ECKey keyPair = keyProv.getECKey();
        session.setEncEcJwkJson(keyPair.toJSONString());
        HeidiWallet wallet = new HeidiWallet(objectMapper,baseUrl);
        //Paradym wallet = new Paradym(objectMapper,baseUrl);
        var claims = wallet.buildJWTClaimsSet(session.getFlowType(),session);


        try {
            String json = claims.toString();
            System.out.println(json);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();
            SignedJWT signedJWT = new SignedJWT(header, claims);
            signedJWT.sign(new RSASSASigner(signingKeys.getPrivateKey()));

            return signedJWT.serialize();

        } catch (Exception e) {
            throw new IllegalStateException("Could not sign OID4VP request object", e);
        }



    }

    public String getOpenid4vpUrl(UUID sessionId) {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));

        String requestUri = baseUrl+"/api/wallet/request/" + session.getId();
        return "openid4vp://authorize?request_uri_method=get&client_id="+baseUrl+"&request_uri=" + requestUri;
    }

    public WalletStatusResponse getStatus(UUID sessionId) {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));

        boolean expired = session.isExpired();
        WalletFlowState state = session.getFlowState();

        if (expired && state != WalletFlowState.VERIFIED) {
            state = WalletFlowState.EXPIRED;
        }

        return new WalletStatusResponse(
                session.getId(),
                state.name(),
                state == WalletFlowState.VERIFIED,
                expired,
                session.getFlowType()
        );
    }

    public WalletRegSuccessResponse getSuccessfullRegResponse(UUID sessionId) {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));
        ClientSettingsDto dto = clientSettingsLookupApi.lookupClientSettingsWithClientId(session.getClientId());


        return new WalletRegSuccessResponse(
                dto.baseUrl()
        );
    }



}
