package com.camo.auth_gateway.backendbridge.api.dto;

public class BackendRegistrationFailedException extends RuntimeException {
    public BackendRegistrationFailedException(String message) {
        super(message);
    }
}
