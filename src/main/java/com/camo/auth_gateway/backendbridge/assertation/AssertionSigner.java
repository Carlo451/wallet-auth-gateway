package com.camo.auth_gateway.backendbridge.assertation;

import com.camo.auth_gateway.backendbridge.config.BridgeSigningProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;

@Component
@NullMarked
public class AssertionSigner {

    private final RSAPrivateKey privateKey;
    private final BridgeSigningProperties signingProperties;

    public AssertionSigner(RSAPrivateKey privateKey,
                           BridgeSigningProperties signingProperties) {
        this.privateKey = privateKey;
        this.signingProperties = signingProperties;
    }

    public String sign(JWTClaimsSet claimsSet) {
        try {
            SignedJWT signedJwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(signingProperties.keyId())
                            .build(),
                    claimsSet
            );

            signedJwt.sign(new RSASSASigner(privateKey));
            return signedJwt.serialize();
        } catch (JOSEException e) {
            throw new AssertionSigningException("Failed to sign bridge assertion", e);
        }
    }
}