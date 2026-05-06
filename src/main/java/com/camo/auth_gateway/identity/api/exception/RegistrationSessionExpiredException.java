package com.camo.auth_gateway.identity.api.exception;

public class RegistrationSessionExpiredException extends RegistrationException{
    public RegistrationSessionExpiredException(String sessionId) {
        super("Registration Session with id: '%s' expired.".formatted(sessionId));
    }
}
