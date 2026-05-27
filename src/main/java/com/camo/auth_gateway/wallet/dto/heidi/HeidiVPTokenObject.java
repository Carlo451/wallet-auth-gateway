package com.camo.auth_gateway.wallet.dto.heidi;


import com.nimbusds.jwt.SignedJWT;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

public class HeidiVPTokenObject  implements WalletVPTokenObject {
    List<DecodedDisclosure> decodedDisclosureList = new ArrayList<>();
    SignedJWT sdJwt;
    SignedJWT keyBinding;

    public static HeidiVPTokenObject parseVpToken(String identifier, String vpToken, ObjectMapper objectMapper) throws Exception {
        HeidiVPTokenObject object = new HeidiVPTokenObject();
        JsonNode root = objectMapper.readTree(vpToken);


        JsonNode sdJwtArray = root.get("ec-pid-hcr1h_dc__sd-jwt");
        String combined = sdJwtArray.get(0).asText();
        String[] parts = combined.split("~");

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

        object.setSdJwt(SignedJWT.parse(parts[0]));
        object.setKeyBinding(SignedJWT.parse(keyBindingJwt));


        for (String disclosure : disclosures) {
            String digest = disclosureDigest(disclosure);

            List<Object> disclosureJson = decodeDisclosure(disclosure,objectMapper);
            if (disclosureJson.size() < 3) {
                throw new IllegalStateException("Unexpected disclosure structure: " + disclosureJson);
            }

            String salt = String.valueOf(disclosureJson.get(0));
            String claimName = String.valueOf(disclosureJson.get(1));
            String claimValue = String.valueOf(disclosureJson.get(2));
            DecodedDisclosure decodedDisclosure = new DecodedDisclosure();

            decodedDisclosure.setSalt(salt);
            decodedDisclosure.setClaimName(claimName);
            decodedDisclosure.setClaimValue(claimValue);
            decodedDisclosure.setDigest(digest);


            object.getDecodedDisclosureList().add(decodedDisclosure);



        }
        return object;

    }

    private static List<Object> decodeDisclosure(String encodedDisclosure, ObjectMapper objectMapper) throws Exception {
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

    private static boolean looksLikeCompactJwt(String value) {
        long dots = value.chars().filter(ch -> ch == '.').count();
        return dots == 2;
    }


    public List<DecodedDisclosure> getDecodedDisclosureList() {
        return decodedDisclosureList;
    }

    public void setDecodedDisclosureList(List<DecodedDisclosure> decodedDisclosureList) {
        this.decodedDisclosureList = decodedDisclosureList;
    }

    public SignedJWT getSdJwt() {
        return sdJwt;
    }

    public void setSdJwt(SignedJWT sdJwt) {
        this.sdJwt = sdJwt;
    }

    public SignedJWT getKeyBinding() {
        return keyBinding;
    }

    public void setKeyBinding(SignedJWT keyBinding) {
        this.keyBinding = keyBinding;
    }

    @Override
    public List<DecodedDisclosure> getDecodedClaims() {
        return decodedDisclosureList;
    }

    @Override
    public DecodedDisclosure findDisclosureObjectForClaimName(String claimName) {
        for(DecodedDisclosure decodedDisclosure : decodedDisclosureList) {
            if(decodedDisclosure.getClaimName().toLowerCase().equals(claimName.toLowerCase())) {
                return decodedDisclosure;
            }
        }
        throw new IllegalStateException("No decoded claims found for claim name: " + claimName);
    }


}
