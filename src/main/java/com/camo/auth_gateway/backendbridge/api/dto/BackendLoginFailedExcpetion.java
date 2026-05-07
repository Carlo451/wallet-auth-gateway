package com.camo.auth_gateway.backendbridge.api.dto;

public class BackendLoginFailedExcpetion extends RuntimeException {
    public BackendLoginFailedExcpetion(String message) {
        super(message);
    }
}
