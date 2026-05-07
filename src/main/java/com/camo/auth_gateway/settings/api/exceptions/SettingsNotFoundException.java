package com.camo.auth_gateway.settings.api.exceptions;

public class SettingsNotFoundException extends RuntimeException {
    public SettingsNotFoundException(String message) {
        super(message);
    }
}
