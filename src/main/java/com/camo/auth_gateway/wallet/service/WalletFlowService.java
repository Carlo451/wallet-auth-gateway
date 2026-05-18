package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.common.config.keys.ECKeyProvider;
import com.camo.auth_gateway.common.config.keys.SigningKeys;
import com.camo.auth_gateway.wallet.domain.WalletFlowState;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.dto.authrequestobj.*;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    public String getRequestObject(UUID sessionId) throws JOSEException {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));
        ECKey keyPair = keyProv.getECKey();
        session.setEncEcJwkJson(keyPair.toJSONString());
        DcqlQuery dcqlQuery = getDcqlQuery(session.getFlowType(),keyPair);




        ClientMetadata clientMetadata = new ClientMetadata();
        Map<String, Map<String,List<String>>> vpFormatsSupported = new HashMap<>();
        vpFormatsSupported.put("dc+sd-jwt",Map.of("sd-jwt_alg_values",List.of("ES256","ES384","ES512","EdDSA"),"kb-jwt_alg_values",List.of("ES256","ES384","ES512","EdDSA")));
        clientMetadata.setVpFormatsSupported(vpFormatsSupported);
        clientMetadata.setAuthorizationEncryptedResponseAlg("ECDH-ES");
        clientMetadata.setAuthorizationEncryptedResponseEnc("A256GCM");

        ECKey responseKeyPair = keyPair.toPublicJWK();
        Map<String,String> key  = new HashMap<>();
        key.put("kty","EC");
        key.put("use","enc");
        key.put("crv","P-256");
        key.put("x",responseKeyPair.getX().toString());
        key.put("y",responseKeyPair.getY().toString());
        key.put("alg","ECDH-ES");
        clientMetadata.setJwks(Map.of("keys",List.of(key)));


        Instant now = Instant.now();


        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(session.getClientId())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(300)))
                .jwtID(UUID.randomUUID().toString())
                .claim("client_id", session.getClientId())
                .claim("response_type", "vp_token")
                .claim("response_mode", "direct_post")
                .claim("nonce", session.getNonce())
                .claim("state", session.getState())
                .claim("aud", "https://self-issued.me/v2")
                .claim("response_uri", baseUrl+"/api/wallet/callback")
                .claim("dcql_query", objectMapper.convertValue(dcqlQuery,new TypeReference<Map<String, Object>>() {}))
                .claim("client_metadata", objectMapper.convertValue(clientMetadata, new TypeReference<Map<String, Object>>() {}))
                .build();
        session.setEncEcJwkJson(responseKeyPair.toJSONString());
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

    public DcqlQuery getDcqlQuery(WalletFlowType flowType, ECKey key) {
        if (WalletFlowType.LOGIN.equals(flowType)) {
            return getDcqlQueryForLogin();
        } else if (WalletFlowType.REGISTRATION.equals(flowType)) {
            return getDcqlQueryForRegistration();
        }
        throw new IllegalArgumentException("Unknown flow type: " + flowType);
    }


    public DcqlQuery getDcqlQueryForRegistration() {
        DcqlQuery dcqlQuery = new DcqlQuery();
        DcqlClaimQuery givenNameClaim = new DcqlClaimQuery();
        //givenNameClaim.setId("1751");
        givenNameClaim.setPath(List.of("given_name"));

        DcqlClaimQuery nameClaim = new DcqlClaimQuery();
        //nameClaim.setId("1752");
        nameClaim.setPath(List.of("family_name"));

        // je nach Credential-Schema evtl. "birthdate"

        DcqlCredentialMetaVctValues meta = new DcqlCredentialMetaVctValues();
        meta.setVctValues(List.of("urn:eu.europa.ec.eudi.pid.1"));

        DcqlCredentialQuery credentialQuery = new DcqlCredentialQuery();
        credentialQuery.setId("ec-pid-hcr1h_dc__sd-jwt");
        credentialQuery.setFormat("dc+sd-jwt");
        credentialQuery.setMeta(meta);
        credentialQuery.setMultiple(false);
        credentialQuery.setRequireCryptographicHolderBinding(false);
        credentialQuery.setClaims(List.of());
        credentialQuery.setClaims(List.of(givenNameClaim,nameClaim));




        dcqlQuery.setCredentials(List.of(credentialQuery));

        dcqlQuery.setCredentialSets(List.of(Map.of("options",List.of(List.of("ec-pid-hcr1h_dc__sd-jwt")))));
        return dcqlQuery;
    }

    public DcqlQuery getDcqlQueryForLogin() {
        DcqlQuery dcqlQuery = new DcqlQuery();

        DcqlCredentialMetaVctValues meta = new DcqlCredentialMetaVctValues();
        meta.setVctValues(List.of("urn:eu.europa.ec.eudi.pid.1"));

        DcqlCredentialQuery credentialQuery = new DcqlCredentialQuery();
        credentialQuery.setId("ec-pid-hcr1h_dc__sd-jwt");
        credentialQuery.setFormat("dc+sd-jwt");
        credentialQuery.setMeta(meta);
        credentialQuery.setMultiple(false);
        credentialQuery.setRequireCryptographicHolderBinding(false);
        credentialQuery.setClaims(List.of());

        dcqlQuery.setCredentials(List.of(credentialQuery));

        dcqlQuery.setCredentialSets(List.of(Map.of("options",List.of(List.of("ec-pid-hcr1h_dc__sd-jwt")))));
        return dcqlQuery;
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
                expired
        );
    }
}
