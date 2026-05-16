package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DcqlCredentialMetaCredsType implements IDcqlCredentialMeta{

    @JsonProperty("credential_types")
    private List<String> vctValues;

    public DcqlCredentialMetaCredsType() {
    }

    public DcqlCredentialMetaCredsType(List<String> vctValues) {
        this.vctValues = vctValues;
    }

    public List<String> getVctValues() {
        return vctValues;
    }

    public void setVctValues(List<String> vctValues) {
        this.vctValues = vctValues;
    }
}