package com.camo.auth_gateway.common.config.keys;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class ECKeyProvider {

    public ECKey getECKey() throws JOSEException {


        ECKey ecJWK = new ECKeyGenerator(Curve.P_256)
                .keyUse(com.nimbusds.jose.jwk.KeyUse.ENCRYPTION)
                .algorithm(new com.nimbusds.jose.Algorithm("ECDH-ES"))
                .keyID(UUID.randomUUID().toString())
                .generate();


        return  ecJWK;
    }
}
