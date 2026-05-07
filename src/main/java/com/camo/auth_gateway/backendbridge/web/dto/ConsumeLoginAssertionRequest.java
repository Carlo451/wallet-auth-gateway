package com.camo.auth_gateway.backendbridge.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ConsumeLoginAssertionRequest(
        @NotBlank String jti,
        @NotBlank String clientId
) {
}