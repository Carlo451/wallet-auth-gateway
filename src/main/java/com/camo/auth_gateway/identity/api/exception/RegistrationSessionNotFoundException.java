package com.camo.auth_gateway.identity.api.exception;

public class RegistrationSessionNotFoundException extends RegistrationException{
    public RegistrationSessionNotFoundException(String sessionId) {
        super("Registration Session with id: '%s' was not found.".formatted(sessionId));
    }
}
