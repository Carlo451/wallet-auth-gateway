package com.camo.auth_gateway.backendbridge.service;

import com.camo.auth_gateway.backendbridge.api.RegistrationBridgeApi;
import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeCommand;
import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeResult;
import com.camo.auth_gateway.backendbridge.assertation.AssertionSigner;
import com.camo.auth_gateway.backendbridge.assertation.RegistrationAssertionFactory;
import com.camo.auth_gateway.backendbridge.http.RegistrationBackendClient;
import com.camo.auth_gateway.backendbridge.http.dto.RegistrationAssertionRequest;
import com.camo.auth_gateway.backendbridge.http.dto.RegistrationBackendResponse;
import com.camo.auth_gateway.settings.api.ClientSettingsLookupApi;
import com.camo.auth_gateway.settings.api.dto.ClientSettingsDto;
import com.nimbusds.jwt.JWTClaimsSet;


public class RegistrationBridgeService implements RegistrationBridgeApi {
    private final ClientSettingsLookupApi clientSettingsLookupApi;
    private final RegistrationBackendClient registrationBackendClient;
    private final RegistrationAssertionFactory registrationAssertionFactory;
    private final AssertionSigner assertionSigner;

    public RegistrationBridgeService(ClientSettingsLookupApi clientSettingsLookupApi,RegistrationBackendClient registrationBackendClient,
                                     RegistrationAssertionFactory registrationAssertionFactory, AssertionSigner assertionSigner) {
        this.clientSettingsLookupApi = clientSettingsLookupApi;
        this.registrationBackendClient = registrationBackendClient;
        this.registrationAssertionFactory = registrationAssertionFactory;
        this.assertionSigner = assertionSigner;
    }
    @Override
    public RegistrationBridgeResult register(RegistrationBridgeCommand command) {
        ClientSettingsDto clientSettings = clientSettingsLookupApi.lookupClientSettingsWithClientId(command.clientId());
        JWTClaimsSet claimsSet = registrationAssertionFactory.create(clientSettings,command);
        RegistrationAssertionRequest registrationAssertionRequest = new RegistrationAssertionRequest(assertionSigner.sign(claimsSet));
        RegistrationBackendResponse registerClientResponse = registrationBackendClient.register(clientSettings.registrationEndpoint(),registrationAssertionRequest);
        return new RegistrationBridgeResult(registerClientResponse.externalUserId());
    }
}
