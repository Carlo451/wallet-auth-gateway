package com.camo.auth_gateway.backendbridge.web.dto;

import com.camo.auth_gateway.backendbridge.service.ConsumeLoginAssertionReason;

import java.time.Instant;

public record ConsumeLoginAssertionResponse(
        String jti,
        String status,
        Instant consumedAt,
        ConsumeLoginAssertionReason reason
) {
}