package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DcqlQuery {

    private List<DcqlCredentialQuery> credentials;

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
}
