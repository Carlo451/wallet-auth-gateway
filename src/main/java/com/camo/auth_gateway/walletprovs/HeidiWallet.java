package com.camo.auth_gateway.walletprovs;

import com.camo.auth_gateway.identity.api.IdentityCreationApi;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.authrequestobj.*;
import com.camo.auth_gateway.wallet.dto.heidi.DecodedDisclosure;
import com.camo.auth_gateway.wallet.dto.heidi.HeidiVPTokenObject;
import com.camo.auth_gateway.wallet.dto.heidi.WalletVPTokenObject;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.KeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;


@Component
public class HeidiWallet implements WalletProvider<HeidiVPTokenObject> {
    private final ObjectMapper objectMapper;

    private final IdentityCreationApi identityCreationApi;

    private String baseUrl;

    public HeidiWallet(ObjectMapper objectMapper,IdentityCreationApi identityCreationApi,
                       @Value("${gateway.app.network.baseurl}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.identityCreationApi = identityCreationApi;
    }

    @Override
    public JWTClaimsSet buildJWTClaimsSet(WalletFlowType flowType, WalletSession session) throws Exception {
        ECKey keyPair = ECKey.parse(session.getEncEcJwkJson());
        DcqlQuery dcqlQuery = getDcqlQuery(session.getFlowType());




        ClientMetadata clientMetadata = new ClientMetadata();
        Map<String, Map<String, List<String>>> vpFormatsSupported = new HashMap<>();
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


        return new JWTClaimsSet.Builder()
                .issuer(session.getClientId())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(300)))
                .jwtID(UUID.randomUUID().toString())
                .claim("client_id", "https://camo-framework.gentoo-fiordland.ts.net:8443")
                .claim("response_type", "vp_token")
                .claim("response_mode", "direct_post")
                .claim("nonce", session.getNonce())
                .claim("state", session.getState())
                .claim("aud", "https://self-issued.me/v2")
                .claim("response_uri", baseUrl+"/api/wallet/callback")
                .claim("dcql_query", objectMapper.convertValue(dcqlQuery,new TypeReference<Map<String, Object>>() {}))
                .claim("client_metadata", objectMapper.convertValue(clientMetadata, new TypeReference<Map<String, Object>>() {}))
                .build();
    }

    @Override
    public String buildWalletIdFromWalletDisclosures(HeidiVPTokenObject tokenObject) throws Exception {
        boolean keyBinding = tokenObject.verifyKeyBinding();
        if (!keyBinding) throw new KeyException("The Key Binding is not valid");

        DecodedDisclosure givenNameDisc = tokenObject.findDisclosureObjectForClaimName("given_name");
        DecodedDisclosure famNameDisc = tokenObject.findDisclosureObjectForClaimName("family_name");
        DecodedDisclosure birthPlaceDisc = tokenObject.findDisclosureObjectForClaimName("birth_place");
        DecodedDisclosure birthDateDisc = tokenObject.findDisclosureObjectForClaimName("birth_date");
        return identityCreationApi.buildIdentityLink(givenNameDisc.getClaimValue(),famNameDisc.getClaimValue(),birthPlaceDisc.getClaimValue(), birthDateDisc.getClaimValue());
    }


    /// Builds the dcql query object
    /// @param flowType specifies if login or registration is wanted
    /// @return
    public DcqlQuery getDcqlQuery(WalletFlowType flowType) {
        if (WalletFlowType.LOGIN.equals(flowType)) {
            return getDcqlQueryForRegistration();
        } else if (WalletFlowType.REGISTRATION.equals(flowType)) {
            return getDcqlQueryForRegistration();
        }
        throw new IllegalArgumentException("Unknown flow type: " + flowType);
    }

    /// Builds dcql query for registration
    /// @return
    public DcqlQuery getDcqlQueryForRegistration() {
        DcqlQuery dcqlQuery = new DcqlQuery();
        DcqlClaimQuery givenNameClaim = new DcqlClaimQuery();
        //givenNameClaim.setId("1751");
        givenNameClaim.setPath(List.of("given_name"));

        DcqlClaimQuery nameClaim = new DcqlClaimQuery();
        //nameClaim.setId("1752");
        nameClaim.setPath(List.of("family_name"));

        DcqlClaimQuery birthPlace = new DcqlClaimQuery();
        //nameClaim.setId("1752");
        birthPlace.setPath(List.of("birth_place"));

        DcqlClaimQuery birthDate = new DcqlClaimQuery();
        //nameClaim.setId("1752");
        birthDate.setPath(List.of("birth_date"));



        DcqlCredentialMetaVctValues meta = new DcqlCredentialMetaVctValues();
        meta.setVctValues(List.of("urn:eu.europa.ec.eudi.pid.1"));

        DcqlCredentialQuery credentialQuery = new DcqlCredentialQuery();
        credentialQuery.setId("ec-pid-hcr1h_dc__sd-jwt");
        credentialQuery.setFormat("dc+sd-jwt");
        credentialQuery.setMeta(meta);
        credentialQuery.setMultiple(false);
        credentialQuery.setRequireCryptographicHolderBinding(true);
        credentialQuery.setClaims(List.of());
        credentialQuery.setClaims(List.of(givenNameClaim,nameClaim,birthPlace,birthDate));




        dcqlQuery.setCredentials(List.of(credentialQuery));

        dcqlQuery.setCredentialSets(List.of(Map.of("options",List.of(List.of("ec-pid-hcr1h_dc__sd-jwt")))));
        return dcqlQuery;
    }

    /// Build dcql query for login
    /// @return
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
}
