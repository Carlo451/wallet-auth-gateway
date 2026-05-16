package com.camo.auth_gateway.backendbridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class BridgeKeyConfig {

    @Bean
    RSAPrivateKey bridgePrivateKey() throws Exception {
        String pem = """
                -----BEGIN PRIVATE KEY-----
                                         MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCtR8ezNoMHh/T+
                                         e4070o1hNEQia9vFZcrzssdONyeC/kGt1S5+sJCOi8FUbqItDh31X305OnAiVsfC
                                         cYidf9K7pFXcNnIam5c+ayhwQKarFAql8Ah661QJxvy7PINBRByuBHWTW5nZdhBK
                                         PKPxoi0OnqSkOiXeCbI+gVsxi5vEJqnhbw+ct/tnkcfpWJjQ3WskSvkxnmee0yhc
                                         DMI9X9MeipwbMCNkR3kKWpMUS3UkM2e5ts2QFJXHOeVKg8pVQDIlhQB+hxWrEBVq
                                         /wzW3fE7UcQpaokNC+5gp45ph1rwNxMBvA4CEE43T8iit7unLTEgKwhbQacd10VL
                                         33nz1+1lAgMBAAECggEAHTk7ngcdwo2knS2fpFzucJwUuJxpqGkyOPboA5IsNPyl
                                         /Yf0SzQCcTPzM0bkTYZxuYH098uaAUSS9IFaybs2wmQlAdph1Ih0kbeXTpUstrIO
                                         2Kz6dSaOmgaH9ZjvRvjSgGuqm6NJd2IgvbBr8tLH2A0b1cGjxMZsW/vLcjfqGYKn
                                         u1rqeKjiZFgufiPZsszxVwoVm+AKB5JESR/Bd2SLXCeqTWEI951+FmVPgyVRfgof
                                         +hutpmrqlIj8Ywxljo/D17WShm0KYjykKtXzyGrOrctLtnwXedHM/FXciTRtqpfl
                                         YZATLIMoHM/ECjhkHRyMd161tgmwPc03QGweRGNgUQKBgQDV/vI2IlFoi8sBj9e3
                                         UuYReCb+Cemyn5coV1cmrvxGGRpFmIL06doiuJK+DXJOjfl7+st9RFwKVOH7l8ST
                                         dBMd7IWbb5s1YiNLiYkm1j5eCx4qVs+IuZEk7E9zLXicvDSJVUFZnDdi6S1fPmaD
                                         AVeC/QpIs+HPR5LO5W/l4b2mdQKBgQDPSu0McY/jF0nkE3FslfzEUJz8V9ObXDuv
                                         Vlj1e9MV9EIRVk7twtZQDQ/0MRfeijzE9kQjb2rQckRAHRNVxzjHITH5cYfW688I
                                         rCgxsp/Wzm/SLhk6OIBhEsSEesk1eT6URWqwTss40QGitiSpf0LPc4wCJqZniDmo
                                         Bs3sSxatMQKBgBlz6HU94bbwoaW+6SZ56rl7NBAVN8GyL7VxpgEI4SKpS7GpcTIW
                                         OobUs0d9q/Hh+yGe8+MjZVspO3PEWnI7ZSazAjU5shlIYfBTHIgNBYAEDIN2rdqS
                                         T8w3ez+00dq6m/kVtd8lsITIPUhN0L2WREVlOXrqQlp4JeML8SJDll4dAoGBALcb
                                         xWefjO2ZrdDFiATWPNChMPsLDcYH5EUO7kfymFSlThMIfXIzEKLn1vbqwt32mJWX
                                         wDrY+a6lsTo9NM4pzDLCVOW2brrf1CMiB+NWmTDjbA1qYSh3rpeRP3yTBdexgSDy
                                         bLrO718B6QL1CE+dNcQQmlVwFMARqnSNhis7bQ2RAoGALLnYT2iSvSrJe6knnI2s
                                         lGueSjCNnltR7NqjjW/mAKXm3buF8s3fsUOZap7ohCvwRdoS04BKj3Yj751NTjgg
                                         aSYHv2AGy/JoELskAFBNIjcosmqRtRgoJMJmuvHw/oPZ6Ln84ku5lPL0XfpAaVSV
                                         PExYh8YvTT/uqdDNtu/z0oY=
                                         -----END PRIVATE KEY-----
                """;

        String normalized = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        return (RSAPrivateKey) privateKey;
    }
}