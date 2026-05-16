package com.camo.auth_gateway.common.config.keys;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class SigningKeys {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public SigningKeys() throws Exception {
        this.privateKey = PemKeyLoader.loadPrivateKey(
                new ClassPathResource("keys/private-key-wallet-sign.pem").getFile().toPath()
        );
        this.publicKey = PemKeyLoader.loadPublicKey(
                new ClassPathResource("keys/public-key-wallet-sign.pem").getFile().toPath()
        );
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
