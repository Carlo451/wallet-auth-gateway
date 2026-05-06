package com.camo.auth_gateway.identity.api.exception;

public class ExternalUserIdAlreadyLinkedException extends IdentityException {
    public ExternalUserIdAlreadyLinkedException(String clientId, String externalUserId) {
        super("External user id '%s' is already linked for client '%s'".formatted(externalUserId, clientId));
    }
}

