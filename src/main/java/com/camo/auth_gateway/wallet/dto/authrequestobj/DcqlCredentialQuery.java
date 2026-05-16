package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DcqlCredentialQuery {

    private String id;
    private String format;
    private IDcqlCredentialMeta meta;
    private List<DcqlClaimQuery> claims;
    @JsonProperty("require_cryptographic_holder_binding")
    private boolean requireCryptographicHolderBinding;
    private boolean multiple;


    @JsonProperty("claim_sets")
    private List<List<String>> claimSets;

    public DcqlCredentialQuery() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public IDcqlCredentialMeta getMeta() {
        return meta;
    }

    public void setMeta(IDcqlCredentialMeta meta) {
        this.meta = meta;
    }

    public List<DcqlClaimQuery> getClaims() {
        return claims;
    }

    public void setClaims(List<DcqlClaimQuery> claims) {
        this.claims = claims;
    }

    public List<List<String>> getClaimSets() {
        return claimSets;
    }

    public void setClaimSets(List<List<String>> claimSets) {
        this.claimSets = claimSets;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isRequireCryptographicHolderBinding() {
        return requireCryptographicHolderBinding;
    }

    public void setRequireCryptographicHolderBinding(boolean requireCryptographicHolderBinding) {
        this.requireCryptographicHolderBinding = requireCryptographicHolderBinding;
    }
}