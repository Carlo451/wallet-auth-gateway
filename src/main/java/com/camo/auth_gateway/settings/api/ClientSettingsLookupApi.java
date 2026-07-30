package com.camo.auth_gateway.settings.api;

import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.camo.auth_gateway.settings.api.exceptions.SettingsNotFoundException;

public interface ClientSettingsLookupApi {
    /// is used to retrieve the information of one client, an external application
    /// @param clientId identification of a client
    /// @return ClientSettingsDto
    /// @throws SettingsNotFoundException, if the client does not exists
    ClientSettingsDto lookupClientSettingsWithClientId(String clientId) throws SettingsNotFoundException;

}
