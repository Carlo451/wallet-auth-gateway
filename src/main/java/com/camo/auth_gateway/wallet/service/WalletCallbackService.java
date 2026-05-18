package com.camo.auth_gateway.wallet.service;


import com.camo.auth_gateway.identity.api.IdentityLookupApi;
import com.camo.auth_gateway.identity.api.model.PseudonymMappingDto;
import com.camo.auth_gateway.wallet.domain.VerifiedClaims;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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

        // TODO:
        // 1. vp_token kryptographisch prüfen
        // 2. nonce gegen session.getNonce() prüfen
        // 3. audience / client_id prüfen
        // 4. presentation_submission prüfen
        // 5. Claims aus vp_token extrahieren
        ECKey key = ECKey.parse(session.getEncEcJwkJson());

        JsonNode root = objectMapper.readTree(vpToken);


        JsonNode sdJwtArray = root.get("ec-pid-hcr1h_dc__sd-jwt");
        String combined = sdJwtArray.get(0).asText();
        String[] parts = combined.split("~");

        boolean valid = checkAndVerifyFirstSegment(parts[0]);

        List<String> disclosures = new ArrayList<>();
        String keyBindingJwt = null;
        List<Map<String, Object>> decodedDisclosures = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String seg = parts[i];
            if (seg == null || seg.isBlank()) {
                continue;
            }
            if (looksLikeCompactJwt(seg)) {
                keyBindingJwt = seg;
            } else {
                disclosures.add(seg);
            }
        }

        for (String disclosure : disclosures) {
            String digest = disclosureDigest(disclosure);
           /* if (!remainingDigests.contains(digest)) {
                System.out.println("Disclosure digest not referenced in _sd: " + digest);
                continue;
            }*/

            List<Object> disclosureJson = decodeDisclosure(disclosure);
            if (disclosureJson.size() < 3) {
                throw new IllegalStateException("Unexpected disclosure structure: " + disclosureJson);
            }

            String salt = String.valueOf(disclosureJson.get(0));
            String claimName = String.valueOf(disclosureJson.get(1));
            Object claimValue = disclosureJson.get(2);

            Map<String, Object> decoded = new LinkedHashMap<>();
            decoded.put("salt", salt);
            decoded.put("claimName", claimName);
            decoded.put("claimValue", claimValue);
            decoded.put("digest", digest);

            decodedDisclosures.add(decoded);

        }



        //PseudonymMappingDto identDto = identityLookupApi.findActiveMapping(session.getClientId(), vpToken).orElseThrow(() -> new IllegalArgumentException("Invalid vp token"));
        VerifiedClaims verifiedClaims = new VerifiedClaims(
                "demo-subject",
                "Max",
                "Mustermann",
                LocalDate.of(1999, 1, 1),
                true,
                "demo-issuer"
        );


        //session.markVerified(verifiedClaims,identDto.externalUserId());
        walletSessionRepository.save(session);
    }

    private List<Object> decodeDisclosure(String encodedDisclosure) throws Exception {
        byte[] decoded = Base64.getUrlDecoder().decode(padBase64Url(encodedDisclosure));
        String json = new String(decoded, StandardCharsets.UTF_8);
        return objectMapper.readValue(json, new TypeReference<List<Object>>() {});

    }

    private static String padBase64Url(String input) {
        int mod = input.length() % 4;
        if (mod == 2) return input + "==";
        if (mod == 3) return input + "=";
        if (mod == 1) throw new IllegalArgumentException("Invalid base64url string");
        return input;
    }

    private static String disclosureDigest(String encodedDisclosure) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(encodedDisclosure.getBytes(StandardCharsets.US_ASCII));
        return base64UrlNoPadding(digest);
    }

    private static String base64UrlNoPadding(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean looksLikeCompactJwt(String value) {
        long dots = value.chars().filter(ch -> ch == '.').count();
        return dots == 2;
    }

    public boolean checkAndVerifyFirstSegment(String sdJwtJws) throws ParseException, JOSEException {


        SignedJWT sdJwt = SignedJWT.parse(sdJwtJws);

        JWSHeader header = sdJwt.getHeader();
        String alg = header.getAlgorithm().getName(); // ES256
        String typ = header.getType() != null ? header.getType().toString() : null;



        JsonNode payloadJson = objectMapper.readTree(sdJwt.getPayload().toString());
        JsonNode cnf =  payloadJson.get("cnf");
        JsonNode jwk = cnf.get("jwk");
        ECKey jwkResponse = ECKey.parse(jwk.toString());
        boolean signatureValid = sdJwt.verify(new ECDSAVerifier(jwkResponse));
        return signatureValid;

    }
}
