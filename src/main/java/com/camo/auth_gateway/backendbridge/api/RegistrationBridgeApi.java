package com.camo.auth_gateway.backendbridge.api;

import com.camo.auth_gateway.backendbridge.api.dto.BackendRegistrationFailedException;
import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeCommand;
import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeResult;

public interface RegistrationBridgeApi {
    RegistrationBridgeResult register(RegistrationBridgeCommand command) throws BackendRegistrationFailedException;
}
