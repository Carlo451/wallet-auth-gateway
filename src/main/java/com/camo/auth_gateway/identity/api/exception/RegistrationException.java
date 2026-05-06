package com.camo.auth_gateway.identity.api.exception;

public abstract class RegistrationException extends RuntimeException {
    protected RegistrationException(String message) {
        super(message);
    }
}
