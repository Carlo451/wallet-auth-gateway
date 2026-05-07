package com.camo.auth_gateway.settings.service;

import com.camo.auth_gateway.settings.api.ClientSettingsLookupApi;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.api.exceptions.SettingsNotFoundException;
import com.camo.auth_gateway.settings.repository.ClientSettingRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsLookupService implements ClientSettingsLookupApi {

    private final ClientSettingRepository clientSettingRepository;

    public SettingsLookupService(ClientSettingRepository clientSettingRepository) {
        this.clientSettingRepository = clientSettingRepository;
    }
    @Override
    public ClientSettingsDto lookupClientSettingsWithClientId(String clientId) throws SettingsNotFoundException {
        return ClientSettingsDto.from(
                clientSettingRepository.findByClientId(clientId)
                        .orElseThrow(() -> new SettingsNotFoundException("No client settings were found with the following client id: '%s'".formatted(clientId)))
        );
    }

    @Override
    public ClientSettingsDto lookupClientSettingsWithBaseUrl(String baseUrl) throws SettingsNotFoundException {
        return ClientSettingsDto.from(
                clientSettingRepository.findByBaseUrl(baseUrl)
                        .orElseThrow(() -> new SettingsNotFoundException("No client settings were found with the following client base url: '%s'".formatted(baseUrl)))
        );
    }
}
