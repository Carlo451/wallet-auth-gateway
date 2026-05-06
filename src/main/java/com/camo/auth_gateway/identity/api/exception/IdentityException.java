package com.camo.auth_gateway.identity.api.exception;

public abstract class IdentityException extends RuntimeException {
    protected IdentityException(String message) {
        super(message);
    }
}
