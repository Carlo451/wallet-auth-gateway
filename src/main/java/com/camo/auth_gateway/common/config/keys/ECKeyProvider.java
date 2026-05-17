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

    public String getJson() {
        return "{\n" +
                "   \"response_uri\":\"https://heidi-verifier-ws-prod.ubique.ch/v1/wallet/authorization\",\n" +
                "   \"aud\":\"https://self-issued.me/v2\",\n" +
                "   \"iss\":\"x509_san_dns:heidi-verifier-ws-prod.ubique.ch\",\n" +
                "   \"response_type\":\"vp_token\",\n" +
                "   \"state\":\"e7b4b46c-0b9b-4c27-a149-ee2b427f9573\",\n" +
                "   \"dcql_query\":{\n" +
                "      \"credentials\":[\n" +
                "         {\n" +
                "            \"id\":\"bdr-demo-hjvua_dc__sd-jwt\",\n" +
                "            \"format\":\"dc+sd-jwt\",\n" +
                "            \"multiple\":false,\n" +
                "            \"meta\":{\n" +
                "               \"vct_values\":[\n" +
                "                  \"https://demo.pid-issuer.bundesdruckerei.de/credentials/pid/1.0\"\n" +
                "               ]\n" +
                "            },\n" +
                "            \"require_cryptographic_holder_binding\":true,\n" +
                "            \"claims\":[\n" +
                "               {\n" +
                "                  \"id\":\"1751\",\n" +
                "                  \"path\":[\n" +
                "                     \"age_in_years\"\n" +
                "                  ]\n" +
                "               }\n" +
                "            ]\n" +
                "         },\n" +
                "         {\n" +
                "            \"id\":\"bdr-demo-hjvua_mso_mdoc\",\n" +
                "            \"format\":\"mso_mdoc\",\n" +
                "            \"multiple\":false,\n" +
                "            \"meta\":{\n" +
                "               \"doctype_value\":\"eu.europa.ec.eudi.pid.1\"\n" +
                "            },\n" +
                "            \"require_cryptographic_holder_binding\":true,\n" +
                "            \"claims\":[\n" +
                "               {\n" +
                "                  \"id\":\"1751\",\n" +
                "                  \"path\":[\n" +
                "                     \"eu.europa.ec.eudi.pid.1\",\n" +
                "                     \"age_in_years\"\n" +
                "                  ]\n" +
                "               }\n" +
                "            ]\n" +
                "         },\n" +
                "         {\n" +
                "            \"id\":\"bdr-demo-hjvua_bbs-termwise\",\n" +
                "            \"format\":\"bbs-termwise\",\n" +
                "            \"multiple\":false,\n" +
                "            \"meta\":{\n" +
                "               \"credential_types\":[\n" +
                "                  \"https://heidi-entity-ws-prod.ubique.ch/public/v2/schema/bdr-demo-hjvua/1.2.0\"\n" +
                "               ]\n" +
                "            },\n" +
                "            \"require_cryptographic_holder_binding\":true,\n" +
                "            \"claims\":[\n" +
                "               {\n" +
                "                  \"id\":\"1751\",\n" +
                "                  \"path\":[\n" +
                "                     \"http://schema.org/age_in_years\"\n" +
                "                  ]\n" +
                "               }\n" +
                "            ]\n" +
                "         },\n" +
                "         {\n" +
                "            \"id\":\"bdr-demo-hjvua_w3c-vcdm\",\n" +
                "            \"format\":\"vc+sd-jwt\",\n" +
                "            \"multiple\":false,\n" +
                "            \"require_cryptographic_holder_binding\":true,\n" +
                "            \"claims\":[\n" +
                "               {\n" +
                "                  \"id\":\"1751\",\n" +
                "                  \"path\":[\n" +
                "                     \"credentialSubject\",\n" +
                "                     \"age_in_years\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               {\n" +
                "                  \"path\":[\n" +
                "                     \"type\"\n" +
                "                  ]\n" +
                "               }\n" +
                "            ]\n" +
                "         },\n" +
                "         {\n" +
                "            \"id\":\"bdr-demo-hjvua_open-badges\",\n" +
                "            \"format\":\"ldp_vc\",\n" +
                "            \"multiple\":false,\n" +
                "            \"require_cryptographic_holder_binding\":false,\n" +
                "            \"claims\":[\n" +
                "               {\n" +
                "                  \"id\":\"1751\",\n" +
                "                  \"path\":[\n" +
                "                     \"credentialSubject\",\n" +
                "                     \"age_in_years\"\n" +
                "                  ]\n" +
                "               },\n" +
                "               {\n" +
                "                  \"path\":[\n" +
                "                     \"type\"\n" +
                "                  ]\n" +
                "               }\n" +
                "            ]\n" +
                "         }\n" +
                "      ],\n" +
                "      \"credential_sets\":[\n" +
                "         {\n" +
                "            \"options\":[\n" +
                "               [\n" +
                "                  \"bdr-demo-hjvua_dc__sd-jwt\"\n" +
                "               ],\n" +
                "               [\n" +
                "                  \"bdr-demo-hjvua_dc__sd-jwt\"\n" +
                "               ],\n" +
                "               [\n" +
                "                  \"bdr-demo-hjvua_bbs-termwise\"\n" +
                "               ],\n" +
                "               [\n" +
                "                  \"bdr-demo-hjvua_w3c-vcdm\"\n" +
                "               ],\n" +
                "               [\n" +
                "                  \"bdr-demo-hjvua_open-badges\"\n" +
                "               ]\n" +
                "            ]\n" +
                "         }\n" +
                "      ]\n" +
                "   },\n" +
                "   \"nonce\":\"ljyTxySbWdQM31cLsqTGHA\",\n" +
                "   \"client_id\":\"x509_san_dns:heidi-verifier-ws-prod.ubique.ch\",\n" +
                "   \"client_metadata\":{\n" +
                "      \"vp_formats_supported\":{\n" +
                "         \"jwt_vc_json\":{\n" +
                "            \"sd-jwt_alc_values\":[\n" +
                "               \"ES256\",\n" +
                "               \"ES384\",\n" +
                "               \"ES512\",\n" +
                "               \"EdDSA\"\n" +
                "            ],\n" +
                "            \"kb-jwt_alc_values\":[\n" +
                "               \"ES256\",\n" +
                "               \"ES384\",\n" +
                "               \"ES512\",\n" +
                "               \"EdDSA\"\n" +
                "            ]\n" +
                "         },\n" +
                "         \"mso_mdoc\":{\n" +
                "            \"deviceauth_alg_values\":[\n" +
                "               -7,\n" +
                "               -35,\n" +
                "               -36,\n" +
                "               -8\n" +
                "            ],\n" +
                "            \"issuerauth_alg_values\":[\n" +
                "               -7,\n" +
                "               -35,\n" +
                "               -36,\n" +
                "               -8\n" +
                "            ]\n" +
                "         },\n" +
                "         \"dc+sd-jwt\":{\n" +
                "            \"sd-jwt_alg_values\":[\n" +
                "               \"ES256\",\n" +
                "               \"ES384\",\n" +
                "               \"ES512\",\n" +
                "               \"EdDSA\"\n" +
                "            ],\n" +
                "            \"kb-jwt_alg_values\":[\n" +
                "               \"ES256\",\n" +
                "               \"ES384\",\n" +
                "               \"ES512\",\n" +
                "               \"EdDSA\"\n" +
                "            ]\n" +
                "         }\n" +
                "      },\n" +
                "      \"authorization_encrypted_response_alg\":\"ECDH-ES\",\n" +
                "      \"authorization_encrypted_response_enc\":\"A256GCM\",\n" +
                "      \"jwks\":{\n" +
                "         \"keys\":[\n" +
                "            {\n" +
                "               \"kty\":\"EC\",\n" +
                "               \"use\":\"enc\",\n" +
                "               \"crv\":\"P-256\",\n" +
                "               \"x\":\"q483qsEP_LacxLokQJwjFeP478z79FLQKz4Ina7UXnA\",\n" +
                "               \"y\":\"brI5t4BdlFDueRdMDytcUcTgXZJnxX8gmzq-xoMbXA\",\n" +
                "               \"alg\":\"ECDH-ES\"\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   },\n" +
                "   \"zkp\":{\n" +
                "      \"definition\":\"{\"\"bdr-demo-hjvua_bbs-termwise\"\":{\"\"type\"\":\"\"required\"\",\"\"key\"\":\"\"http\":\"}}\",\n" +
                "      \"provingKey\":\"{\"\"bdr-demo-hjvua_bbs-termwise\"\":\"e30\"}\",\n" +
                "      \"issuerPk\":\"zUC711y7V85xqmn7UidKFf5kwC3RWjB9CTsqEWjk81Yqs1TQUs3oSawsQxCU3mdziXmbyrEPs2GFkXqvojqYiWz9JyXHaMjh7bR3XYPJTXgU9FXHDEWMarUAWiRBYu5ZenGmvn\",\n" +
                "      \"issuerId\":\"did:example:issuer0\",\n" +
                "      \"issuerKeyId\":\"did:example:issuer0#bls12_381-g2-pub001\"\n" +
                "   },\n" +
                "   \"response_mode\":\"direct_post.jwt\",\n" +
                "   \"jwtks\":{\n" +
                "      \"definition\":{\n" +
                "         \"bdr-demo-hjvua_bbs-termwise\":[\n" +
                "            {\n" +
                "               \"\"\"type\"\"\":\"\"\"required\"\"\",\n" +
                "               \"\"\"key\"\"\":\"\"\"http\":\"\"\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      \"provingKey\":\"{\"\"bdr-demo-hjvua_bbs-termwise\"\":\"e30\"}\",\n" +
                "      \"issuerPk\":\"zUC711y7V85xqmn7UidKFf5kwC3RWjB9CTsqEWjk81Yqs1TQUs3oSawsQxCU3mdziXmbyrEPs2GFkXqvojqYiWz9JyXHaMjh7bR3XYPJTXgU9FXHDEWMarUAWiRBYu5ZenGmvn\",\n" +
                "      \"issuerId\":\"did:example:issuer0\",\n" +
                "      \"issuerKeyId\":\"did:example:issuer0#bls12_381-g2-pub001\"\n" +
                "   },\n" +
                "   \"authorization_encrypted_response_alg\":\"ECDH-ES\",\n" +
                "   \"authorization_encrypted_response_enc\":\"A256GCM\",\n" +
                "   \"jwks\":{\n" +
                "      \"keys\":[\n" +
                "         {\n" +
                "            \"kty\":\"EC\",\n" +
                "            \"use\":\"enc\",\n" +
                "            \"crv\":\"P-256\",\n" +
                "            \"x\":\"q483qsEP_LacxLokQJwjFeP478z79FLQKz4Ina7UXnA\",\n" +
                "            \"y\":\"brI5t4BdlFDueRdMDytcUcTgXZJnxX8gmzq-xoMbXA\",\n" +
                "            \"alg\":\"ECDH-ES\"\n" +
                "         }\n" +
                "      ]\n" +
                "   }\n" +
                //"   \"response_mode\":\"direct_post.jwt\"\n" +
                "}";
        /*return "{\n" +
                "  \"response_uri\": \"https://camo-framework.gentoo-fiordland.ts.net:8443/api/wallet/callback\",\n" +
                "  \"aud\": \"https://self-issued.me/v2\",\n" +
                "  \"iss\": \"https://camo-framework.gentoo-fiordland.ts.net:8443\",\n" +
                "  \"response_type\": \"vp_token\",\n" +
                "  \"state\": \"d4c63ff6-e045-4b8b-b338-783cad4542fc\",\n" +
                "  \"dcql_query\": {\n" +
                "    \"credentials\": [\n" +
                "      {\n" +
                "        \"id\": \"bdr-demo-hjvua_dc__sd-jwt\",\n" +
                "        \"format\": \"dc+sd-jwt\",\n" +
                "        \"multiple\": false,\n" +
                "        \"meta\": {\n" +
                "          \"vct_values\": [\n" +
                "            \"https://demo.pid-issuer.bundesdruckerei.de/credentials/pid/1.0\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"require_cryptographic_holder_binding\": true,\n" +
                "        \"claims\": [\n" +
                "          {\n" +
                "            \"id\": \"1751\",\n" +
                "            \"path\": [\n" +
                "              \"age_in_years\"\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"bdr-demo-hjvua_mso_mdoc\",\n" +
                "        \"format\": \"mso_mdoc\",\n" +
                "        \"multiple\": false,\n" +
                "        \"meta\": {\n" +
                "          \"doctype_value\": \"eu.europa.ec.eudi.pid.1\"\n" +
                "        },\n" +
                "        \"require_cryptographic_holder_binding\": true,\n" +
                "        \"claims\": [\n" +
                "          {\n" +
                "            \"id\": \"1751\",\n" +
                "            \"path\": [\n" +
                "              \"eu.europa.ec.eudi.pid.1\",\n" +
                "              \"age_in_years\"\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"bdr-demo-hjvua_bbs-termwise\",\n" +
                "        \"format\": \"bbs-termwise\",\n" +
                "        \"multiple\": false,\n" +
                "        \"meta\": {\n" +
                "          \"credential_types\": [\n" +
                "            \"https://heidi-entity-ws-prod.ubique.ch/public/v2/schema/bdr-demo-hjvua/1.2.0\"\n" +
                "          ]\n" +
                "        },\n" +
                "        \"require_cryptographic_holder_binding\": true,\n" +
                "        \"claims\": [\n" +
                "          {\n" +
                "            \"id\": \"1751\",\n" +
                "            \"path\": [\n" +
                "              \"http://schema.org/age_in_years\"\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"bdr-demo-hjvua_w3c-vcdm\",\n" +
                "        \"format\": \"vc+sd-jwt\",\n" +
                "        \"multiple\": false,\n" +
                "        \"require_cryptographic_holder_binding\": true,\n" +
                "        \"claims\": [\n" +
                "          {\n" +
                "            \"id\": \"1751\",\n" +
                "            \"path\": [\n" +
                "              \"credentialSubject\",\n" +
                "              \"age_in_years\"\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"path\": [\n" +
                "              \"type\"\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"bdr-demo-hjvua_open-badges\",\n" +
                "        \"format\": \"ldp_vc\",\n" +
                "        \"multiple\": false,\n" +
                "        \"require_cryptographic_holder_binding\": false,\n" +
                "        \"claims\": [\n" +
                "          {\n" +
                "            \"id\": \"1751\",\n" +
                "            \"path\": [\n" +
                "              \"credentialSubject\",\n" +
                "              \"age_in_years\"\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"path\": [\n" +
                "              \"type\"\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"credential_sets\": [\n" +
                "      {\n" +
                "        \"options\": [\n" +
                "          [\"bdr-demo-hjvua_dc__sd-jwt\"],\n" +
                "          [\"bdr-demo-hjvua_dc__sd-jwt\"],\n" +
                "          [\"bdr-demo-hjvua_bbs-termwise\"],\n" +
                "          [\"bdr-demo-hjvua_w3c-vcdm\"],\n" +
                "          [\"bdr-demo-hjvua_open-badges\"]\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"nonce\": \"5b76d204-d128-4f38-8417-a04003becb88\",\n" +
                "  \"client_id\": \"https://camo-framework.gentoo-fiordland.ts.net:8443\",\n" +
                "  \"client_metadata\": {\n" +
                "    \"vp_formats_supported\": {\n" +
                "      \"jwt_vc_json\": {\n" +
                "        \"sd-jwt_alc_values\": [\n" +
                "          \"ES256\",\n" +
                "          \"ES384\",\n" +
                "          \"ES512\",\n" +
                "          \"EdDSA\"\n" +
                "        ],\n" +
                "        \"kb-jwt_alc_values\": [\n" +
                "          \"ES256\",\n" +
                "          \"ES384\",\n" +
                "          \"ES512\",\n" +
                "          \"EdDSA\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"mso_mdoc\": {\n" +
                "        \"deviceauth_alg_values\": [\n" +
                "          -7,\n" +
                "          -35,\n" +
                "          -36,\n" +
                "          -8\n" +
                "        ],\n" +
                "        \"issuerauth_alg_values\": [\n" +
                "          -7,\n" +
                "          -35,\n" +
                "          -36,\n" +
                "          -8\n" +
                "        ]\n" +
                "      },\n" +
                "      \"dc+sd-jwt\": {\n" +
                "        \"sd-jwt_alg_values\": [\n" +
                "          \"ES256\",\n" +
                "          \"ES384\",\n" +
                "          \"ES512\",\n" +
                "          \"EdDSA\"\n" +
                "        ],\n" +
                "        \"kb-jwt_alg_values\": [\n" +
                "          \"ES256\",\n" +
                "          \"ES384\",\n" +
                "          \"ES512\",\n" +
                "          \"EdDSA\"\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    \"authorization_encrypted_response_alg\": \"ECDH-ES\",\n" +
                "    \"authorization_encrypted_response_enc\": \"A256GCM\",\n" +
                "    \"jwks\": {\n" +
                "      \"keys\": [\n" +
                "        {\n" +
                "          \"kty\": \"EC\",\n" +
                "          \"use\": \"enc\",\n" +
                "          \"kid\": \"enc-key-1\",\n" +
                "          \"crv\": \"P-256\",\n" +
                "          \"x\": \"q29z8yz1_CFpxG3ZgCPbYDXONkMyp8wNXbGA3hifDYs\",\n" +
                "          \"y\": \"BX_RYWiGSSL_SWwui4BvGm5yr8UCQpBXfrMgoXxtSFw\",\n" +
                "          \"alg\": \"ECDH-ES\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"zkp\": {\n" +
                "    \"definition\": \"{ \\\"bdr-demo-hjvua_bbs-termwise\\\": { \\\"type\\\": \\\"required\\\", \\\"key\\\": \\\"http\\\" } }\",\n" +
                "    \"provingKey\": \"{ \\\"bdr-demo-hjvua_bbs-termwise\\\": \\\"e30\\\" }\",\n" +
                "    \"issuerPk\": \"zUC711y7V85xqmn7UidKFf5kwC3RWjB9CTsqEWjk81Yqs1TQUs3oSawsQxCU3mdziXmbyrEPs2GFkXqvojqYiWz9JyXHaMjh7bR3XYPJTXgU9FXHDEWMarUAWiRBYu5ZenGmvn\",\n" +
                "    \"issuerId\": \"did:example:issuer0\",\n" +
                "    \"issuerKeyId\": \"did:example:issuer0#bls12_381-g2-pub001\"\n" +
                "  },\n" +
                "  \"response_mode\": \"direct_post.jwt\",\n" +
                "  \"jwtks\": {\n" +
                "    \"definition\": {\n" +
                "      \"bdr-demo-hjvua_bbs-termwise\": [\n" +
                "        {\n" +
                "          \"type\": \"required\",\n" +
                "          \"key\": \"http\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    \"provingKey\": \"{ \\\"bdr-demo-hjvua_bbs-termwise\\\": \\\"e30\\\" }\",\n" +
                "    \"issuerPk\": \"zUC711y7V85xqmn7UidKFf5kwC3RWjB9CTsqEWjk81Yqs1TQUs3oSawsQxCU3mdziXmbyrEPs2GFkXqvojqYiWz9JyXHaMjh7bR3XYPJTXgU9FXHDEWMarUAWiRBYu5ZenGmvn\",\n" +
                "    \"issuerId\": \"did:example:issuer0\",\n" +
                "    \"issuerKeyId\": \"did:example:issuer0#bls12_381-g2-pub001\"\n" +
                "  },\n" +
                "  \"authorization_encrypted_response_alg\": \"ECDH-ES\",\n" +
                "  \"authorization_encrypted_response_enc\": \"A256GCM\",\n" +
                "  \"jwks\": {\n" +
                "    \"keys\": [\n" +
                "      {\n" +
                "        \"kty\": \"EC\",\n" +
                "        \"use\": \"enc\",\n" +
                "        \"kid\": \"enc-key-1\",\n" +
                "        \"crv\": \"P-256\",\n" +
                "        \"x\": \"q483qsEP_LacxLokQJwjFeP478z79FLQKz4Ina7UXnA\",\n" +
                "        \"y\": \"brI5t4BdlFDueRdMDytcUcTgXZJnxX8gmzq\",\n" +
                "        \"alg\": \"ECDH-ES\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"response_mode\": \"direct_post.jwt\"\n" +
                "}";*/
    }
}
