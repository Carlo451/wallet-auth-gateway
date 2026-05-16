package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientMetadata {

    @JsonProperty("vp_formats_supported")
    private Map<String, Map<String,List<String>>> vpFormatsSupported;

    @JsonProperty("authorization_encrypted_response_alg")
    private String authorizationEncryptedResponseAlg;

    @JsonProperty("authorization_encrypted_response_enc")
    private String authorizationEncryptedResponseEnc;

    @JsonProperty("jwks")
    private Map<String,List<Map<String,String>>> jwks;


    public ClientMetadata() {
    }

    public Map<String, Map<String,List<String>>> getVpFormatsSupported() {
        return vpFormatsSupported;
    }

    public void setVpFormatsSupported(Map<String, Map<String,List<String>>> vpFormatsSupported) {
        this.vpFormatsSupported = vpFormatsSupported;
    }

    public String getAuthorizationEncryptedResponseAlg() {
        return authorizationEncryptedResponseAlg;
    }

    public void setAuthorizationEncryptedResponseAlg(String authorizationEncryptedResponseAlg) {
        this.authorizationEncryptedResponseAlg = authorizationEncryptedResponseAlg;
    }

    public String getAuthorizationEncryptedResponseEnc() {
        return authorizationEncryptedResponseEnc;
    }

    public void setAuthorizationEncryptedResponseEnc(String authorizationEncryptedResponseEnc) {
        this.authorizationEncryptedResponseEnc = authorizationEncryptedResponseEnc;
    }

    public Map<String, List<Map<String, String>>> getJwks() {
        return jwks;
    }

    public void setJwks(Map<String, List<Map<String, String>>> jwks) {
        this.jwks = jwks;
    }
}