package com.camo.auth_gateway.backendbridge.api;

import com.camo.auth_gateway.backendbridge.service.ConsumeLoginAssertionResult;
import com.camo.auth_gateway.backendbridge.web.dto.ConsumeLoginAssertionRequest;
import com.camo.auth_gateway.backendbridge.web.dto.ConsumeLoginAssertionResponse;

public interface LoginAssertionConsumptionApi {
    ConsumeLoginAssertionResult consume(String jti, String clientId);
}
