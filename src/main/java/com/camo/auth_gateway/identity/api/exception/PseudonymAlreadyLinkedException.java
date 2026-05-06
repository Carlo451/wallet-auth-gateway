package com.camo.auth_gateway.identity.api.exception;

public class PseudonymAlreadyLinkedException extends IdentityException {
    public PseudonymAlreadyLinkedException(String clientId, String pseudonymValue) {
        super("Pseudonym '%s' is already linked for client '%s'".formatted(pseudonymValue, clientId));
    }
}
