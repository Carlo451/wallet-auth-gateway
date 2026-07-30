package com.camo.auth_gateway.backendbridge.config;

import com.camo.auth_gateway.common.config.keys.PemKeyLoader;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
@NullMarked
public class BridgeKeyConfig {

    private final BridgeSigningProperties bridgeSigningProperties;
    @Bean
    RSAPrivateKey bridgePrivateKey() throws Exception {
        return PemKeyLoader.loadPrivateKey(bridgeSigningProperties.walletSignPrivKey());
    }
}