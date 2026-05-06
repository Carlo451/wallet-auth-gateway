package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenId4VpAuthorizationRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("response_type")
    private String responseType = "vp_token";

    @JsonProperty("response_mode")
    private String responseMode = "direct_post";

    @JsonProperty("response_uri")
    private String responseUri;

    private String nonce;
    private String state;

    @JsonProperty("client_metadata")
    private ClientMetadata clientMetadata;

    @JsonProperty("dcql_query")
    private DcqlQuery dcqlQuery;

    public OpenId4VpAuthorizationRequest() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(String responseMode) {
        this.responseMode = responseMode;
    }

    public String getResponseUri() {
        return responseUri;
    }

    public void setResponseUri(String responseUri) {
        this.responseUri = responseUri;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ClientMetadata getClientMetadata() {
        return clientMetadata;
    }

    public void setClientMetadata(ClientMetadata clientMetadata) {
        this.clientMetadata = clientMetadata;
    }

    public DcqlQuery getDcqlQuery() {
        return dcqlQuery;
    }

    public void setDcqlQuery(DcqlQuery dcqlQuery) {
        this.dcqlQuery = dcqlQuery;
    }
}
