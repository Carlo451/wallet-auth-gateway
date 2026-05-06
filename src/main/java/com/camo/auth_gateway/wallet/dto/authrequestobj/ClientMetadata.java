package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientMetadata {

    @JsonProperty("vp_formats_supported")
    private Map<String, Object> vpFormatsSupported;

    public ClientMetadata() {
    }

    public Map<String, Object> getVpFormatsSupported() {
        return vpFormatsSupported;
    }

    public void setVpFormatsSupported(Map<String, Object> vpFormatsSupported) {
        this.vpFormatsSupported = vpFormatsSupported;
    }
}