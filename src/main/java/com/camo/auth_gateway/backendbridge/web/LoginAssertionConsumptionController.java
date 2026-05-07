package com.camo.auth_gateway.backendbridge.web;

import com.camo.auth_gateway.backendbridge.api.LoginAssertionConsumptionApi;
import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeCommand;
import com.camo.auth_gateway.backendbridge.api.dto.RegistrationBridgeResult;
import com.camo.auth_gateway.backendbridge.service.ConsumeLoginAssertionResult;
import com.camo.auth_gateway.backendbridge.web.dto.ConsumeLoginAssertionRequest;
import com.camo.auth_gateway.backendbridge.web.dto.ConsumeLoginAssertionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/api/bridge")
public class LoginAssertionConsumptionController {

    private final LoginAssertionConsumptionApi loginAssertionConsumptionApi;
    public LoginAssertionConsumptionController(LoginAssertionConsumptionApi loginAssertionConsumptionApi) {
        this.loginAssertionConsumptionApi = loginAssertionConsumptionApi;
    }

    @PostMapping("/login-assertion/consume")
    public ResponseEntity<ConsumeLoginAssertionResponse> consume(@RequestBody ConsumeLoginAssertionRequest request) {
        ConsumeLoginAssertionResult result = loginAssertionConsumptionApi.consume(request.jti(),request.clientId());
        String status = result.success() ? "CONSUMED" : "REJECTED";
        ConsumeLoginAssertionResponse response = new ConsumeLoginAssertionResponse(
                result.jti(),
                status,
                result.consumedAt(),
                result.reason()
        );
        return ResponseEntity.ok(response);
    }
}
