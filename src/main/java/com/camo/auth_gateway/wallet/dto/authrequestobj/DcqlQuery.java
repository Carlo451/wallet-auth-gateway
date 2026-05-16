package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DcqlQuery {

    private List<DcqlCredentialQuery> credentials;

    @JsonProperty("credential_sets")
    private List<Map<String, List<List<String>>>> credentialSets;

    public DcqlQuery() {
    }

    public DcqlQuery(List<DcqlCredentialQuery> credentials) {
        this.credentials = credentials;
    }

    public List<DcqlCredentialQuery> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<DcqlCredentialQuery> credentials) {
        this.credentials = credentials;
    }
    public List<Map<String, List<List<String>>>> getCredentialSets() {
        return credentialSets;
    }
    public void setCredentialSets(List<Map<String, List<List<String>>>> credentialSets) {
        this.credentialSets = credentialSets;
    }
}
