package com.camo.auth_gateway.wallet.dto.authrequestobj;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DcqlClaimQuery {

    private String id;
    private List<String> path;
    private List<String> values;

    public DcqlClaimQuery() {
    }

    public DcqlClaimQuery(String id, List<String> path) {
        this.id = id;
        this.path = path;
    }

    public DcqlClaimQuery(String id, List<String> path, List<String> values) {
        this.id = id;
        this.path = path;
        this.values = values;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
