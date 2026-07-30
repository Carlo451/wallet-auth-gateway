package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.common.config.keys.ECKeyProvider;
import com.camo.auth_gateway.common.config.keys.SigningKeys;
import com.camo.auth_gateway.identity.api.IdentityCreationApi;
import com.camo.auth_gateway.settings.api.ClientSettingsLookupApi;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
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
    private final IdentityCreationApi identityCreationApi;



    /// handles the session opening, this includes filling in the information of the request,
    ///  the saving of the session and
    /// creating the request URI link for the openId4VP request Object
    /// @param request StartWalletLoginRequest with the basic informations
    /// @return StartWalletLoginResponse  containing some informations about the session aswell as the openid4VP request URI
    public StartWalletLoginResponse startAuth(StartWalletLoginRequest request) {
        Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);

        WalletSession session = WalletSession.createNew(
                request.redirectUri(),
                expiresAt,
                request.flowType(),
                request.clientId()

        );
        ClientSettingsDto dto = clientSettingsLookupApi.lookupClientSettingsWithClientId(request.clientId());
        if (!dto.enabled()) throw new RuntimeException("Client is not enabled");
        session.markPending();
        walletSessionRepository.save(session);

        String requestUri = baseUrl+"/api/wallet/request/" + session.getId();
        String openid4vpUrl = getOpenid4vpUrl(session.getId());

        return new StartWalletLoginResponse(
                session.getId(),
                session.getFlowState().name(),
                requestUri,
                openid4vpUrl,
                session.getExpiresAt()
        );
    }

    /// Creating the OPENID4VP Request  Object for the Wallet
    /// @param sessionId identifiaction of the session
    /// @return A BASE64-URL String containing the OpenID4VP request object
    /// @throws Exception is thrown when the signing process does not work
    @Transactional
    public String getRequestObject(UUID sessionId) throws Exception {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));
        ECKey keyPair = keyProv.getECKey();
        session.setEncEcJwkJson(keyPair.toJSONString());
        HeidiWallet wallet = new HeidiWallet(objectMapper,identityCreationApi,baseUrl);
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

    /// Creates the request object URI for the OpenID4VP request object
    /// @param sessionId identification of the session
    /// @return the request object URI
    public String getOpenid4vpUrl(UUID sessionId) {

        String requestUri = baseUrl+"/api/wallet/request/" + sessionId;
        return "openid4vp://authorize?request_uri_method=get&client_id="+baseUrl+"&request_uri=" + requestUri;
    }

    /// retrieving the most important information of the session and runs the basic invalidation process when called
    /// @param sessionId identifiaction of session
    /// @return WalletStatusResponse
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

    /// Looks up the informations regarding a redirect to the external application, when registration was successfull
    /// @param sessionId identification of  the session
    /// @return WalletRegSuccessResponse with external application base URL
    public WalletRegSuccessResponse getSuccessfullRegResponse(UUID sessionId) {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));
        ClientSettingsDto dto = clientSettingsLookupApi.lookupClientSettingsWithClientId(session.getClientId());


        return new WalletRegSuccessResponse(
                dto.baseUrl()
        );
    }



}
