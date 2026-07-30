package com.camo.auth_gateway.settings.api.dto;

import com.camo.auth_gateway.settings.domain.AskedClaimForRegister;
import com.camo.auth_gateway.settings.domain.ClientFlowType;
import com.camo.auth_gateway.settings.domain.ClientSettings;

import java.util.List;

public record ClientSettingsDto(
        Long id,
        String clientId,
        String name,
        String description,
        boolean enabled,
        String baseUrl,
        String loginEndpoint,
        String registrationEndpoint,
        String successRedirectUri,
        String errorRedirectUri,
        String audience,
        List<String> requestedClaimsForRegister
) {

    public static ClientSettingsDto from(ClientSettings clientSettings) {
        return new ClientSettingsDto(
                clientSettings.getId(),
                clientSettings.getClientId(),
                clientSettings.getName(),
                clientSettings.getDescription(),
                clientSettings.isEnabled(),
                clientSettings.getBaseUrl(),
                clientSettings.getLoginEndpoint(),
                clientSettings.getRegistrationEndpoint(),
                clientSettings.getSuccessRedirectUri(),
                clientSettings.getErrorRedirectUri(),
                clientSettings.getAudience(),
                List.copyOf(clientSettings.getAskedClaimsForRegister().stream().map(AskedClaimForRegister::getClaimName).toList())
        );
    }
}