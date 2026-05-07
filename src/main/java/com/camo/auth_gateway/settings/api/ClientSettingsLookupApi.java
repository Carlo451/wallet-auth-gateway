package com.camo.auth_gateway.settings.api;

import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.api.exceptions.SettingsNotFoundException;

public interface ClientSettingsLookupApi {

    ClientSettingsDto lookupClientSettingsWithClientId(String clientId) throws SettingsNotFoundException;

    ClientSettingsDto lookupClientSettingsWithBaseUrl(String baseUrl) throws SettingsNotFoundException;
}
