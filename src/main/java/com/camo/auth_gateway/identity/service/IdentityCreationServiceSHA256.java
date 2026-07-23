package com.camo.auth_gateway.identity.service;

import com.camo.auth_gateway.identity.api.IdentityCreationApi;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class IdentityCreationServiceSHA256 implements IdentityCreationApi {


    @Override
    public String buildIdentityLink(String givenName, String familyName, String birthPlace, String birthDate) {

        String input = givenName+familyName+birthPlace+birthDate;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
