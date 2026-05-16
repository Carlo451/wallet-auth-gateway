package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class DcqlCredentialMetaDocType implements IDcqlCredentialMeta {
    @JsonProperty("doctype_value")
    private String vctValues;

    public DcqlCredentialMetaDocType() {
    }

    public DcqlCredentialMetaDocType(String vctValues) {
        this.vctValues = vctValues;
    }

    public String getVctValues() {
        return vctValues;
    }

    public void setVctValues(String vctValues) {
        this.vctValues = vctValues;
    }
}
