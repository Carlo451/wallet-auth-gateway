package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.common.config.keys.ECKeyProvider;
import com.camo.auth_gateway.common.config.keys.SigningKeys;
import com.camo.auth_gateway.wallet.domain.WalletFlowState;
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
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


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

    public String getRequestObject(UUID sessionId) throws JOSEException {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));

        OpenId4VpAuthorizationRequest request = new OpenId4VpAuthorizationRequest();

        request.setClientId(baseUrl);
        request.setResponseType("vp_token");
        request.setResponseMode("direct_post");
        request.setResponseUri(baseUrl+"/api/wallet/callback");
        request.setNonce(session.getNonce());
        request.setState(session.getState());

        DcqlClaimQuery givenNameClaim = new DcqlClaimQuery();
        givenNameClaim.setId("1751");
        givenNameClaim.setPath(List.of("age_in_years"));

        // je nach Credential-Schema evtl. "birthdate"

        DcqlCredentialMetaVctValues meta = new DcqlCredentialMetaVctValues();
        meta.setVctValues(List.of("https://demo.pid-issuer.bundesdruckerei.de/credentials/pid/1.0"));

        DcqlCredentialQuery credentialQuery = new DcqlCredentialQuery();
        credentialQuery.setId("bdr-demo-hjvua_dc__sd-jwt");
        credentialQuery.setFormat("dc+sd-jwt");
        credentialQuery.setMeta(meta);
        credentialQuery.setMultiple(false);
        credentialQuery.setRequireCryptographicHolderBinding(false);
        credentialQuery.setClaims(List.of());
        credentialQuery.setClaims(List.of(givenNameClaim));


        DcqlClaimQuery givenNameClaimMso = new DcqlClaimQuery();
        givenNameClaimMso.setId("1751");
        givenNameClaimMso.setPath(List.of("eu.europa.ec.eudi.pid.1","age_in_years"));

        DcqlCredentialMetaDocType metaDoc = new DcqlCredentialMetaDocType();
        metaDoc.setVctValues("eu.europa.ec.eudi.pid.1");

        DcqlCredentialQuery credentialQueryDoc = new DcqlCredentialQuery();
        credentialQueryDoc.setId("bdr-demo-hjvua_mso_mdoc");
        credentialQueryDoc.setFormat("mso_mdoc");
        credentialQueryDoc.setMeta(metaDoc);
        credentialQueryDoc.setMultiple(false);
        credentialQueryDoc.setRequireCryptographicHolderBinding(false);
        credentialQueryDoc.setClaims(List.of(givenNameClaimMso));


        DcqlClaimQuery givenNameClaimCreds = new DcqlClaimQuery();
        givenNameClaimCreds.setId("1751");
        givenNameClaimCreds.setPath(List.of("http://schema.org/age_in_years"));

        DcqlCredentialMetaCredsType metaCredTyp = new DcqlCredentialMetaCredsType();
        metaCredTyp.setVctValues(List.of("https://heidi-entity-ws-prod.ubique.ch/public/v2/schema/bdr-demo-hjvua/1.2.0"));

        DcqlCredentialQuery credentialQueryCred = new DcqlCredentialQuery();
        credentialQueryCred.setId("bdr-demo-hjvua_bbs-termwise");
        credentialQueryCred.setFormat("bbs-termwise");
        credentialQueryCred.setMeta(metaCredTyp);
        credentialQueryCred.setMultiple(false);
        credentialQueryCred.setRequireCryptographicHolderBinding(false);
        credentialQueryCred.setClaims(List.of());
        credentialQueryCred.setClaims(List.of(givenNameClaimCreds));





        DcqlQuery dcqlQuery = new DcqlQuery();
        //dcqlQuery.setCredentials(List.of(credentialQuery, credentialQueryDoc,credentialQueryCred));
        dcqlQuery.setCredentials(List.of(credentialQuery));

        dcqlQuery.setCredentialSets(List.of(Map.of("options",List.of(List.of("bdr-demo-hjvua_dc__sd-jwt")))));
        request.setDcqlQuery(dcqlQuery);


        RSAKey publicJwk = new RSAKey.Builder(signingKeys.getPublicKey())
                .keyID("bridge-key-1")
                .build();

        Map<String, Object> jwks =Map.of(
                "keys", List.of(publicJwk.toJSONObject())
        );
        ClientMetadata clientMetadata = new ClientMetadata();
        Map<String, Map<String,List<String>>> vpFormatsSupported = new HashMap<>();
        vpFormatsSupported.put("dc+sd-jwt",Map.of("sd-jwt_alg_values",List.of("ES256","ES384","ES512","EdDSA"),"kb-jwt_alg_values",List.of("ES256","ES384","ES512","EdDSA")));
        clientMetadata.setVpFormatsSupported(vpFormatsSupported);
        clientMetadata.setAuthorizationEncryptedResponseAlg("ECDH-ES");
        clientMetadata.setAuthorizationEncryptedResponseEnc("A256GCM");

        Map<String,String> key  = new HashMap<>();
        key.put("kty","EC");
        key.put("use","enc");
        key.put("crv","P-256");
        key.put("x",keyProv.getECKey().getX().toString());
        key.put("y",keyProv.getECKey().getY().toString());
        key.put("alg","ECDH-ES");
        clientMetadata.setJwks(Map.of("keys",List.of(key)));


        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(request.getClientId())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(300)))
                .jwtID(UUID.randomUUID().toString())
                .claim("client_id", request.getClientId())
                .claim("response_type", request.getResponseType())
                .claim("response_mode", request.getResponseMode())
                .claim("nonce", request.getNonce())
                .claim("state", request.getState())
                .claim("aud", "https://self-issued.me/v2")
                .claim("response_uri", request.getResponseUri())
                .claim("dcql_query", objectMapper.convertValue(dcqlQuery,new TypeReference<Map<String, Object>>() {}))
                .claim("client_metadata", objectMapper.convertValue(clientMetadata, new TypeReference<Map<String, Object>>() {}))
                //.claim("jwks", objectMapper.convertValue(clientMetadata.getJwks(),new TypeReference<Map<String, Object>>() {}))
                .build();

        try {
            String json = claims.toString();
            System.out.println(json);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();
            SignedJWT signedJWT = new SignedJWT(header, claims);
            signedJWT.sign(new RSASSASigner(signingKeys.getPrivateKey()));

            return signedJWT.serialize();
            /*Map<String, Object> claimsMap =
                    objectMapper.readValue(keyProv.getJson(), new TypeReference<Map<String, Object>>() {});
            JWTClaimsSet claimss = JWTClaimsSet.parse(claimsMap);
            String json = claimss.toString();
            System.out.println(json);
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();
            SignedJWT signedJWT = new SignedJWT(header, claimss);
            signedJWT.sign(new RSASSASigner(signingKeys.getPrivateKey()));
            return signedJWT.serialize();*/

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
                expired
        );
    }
}
