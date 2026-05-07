package com.camo.auth_gateway.backendbridge.service;


import java.time.Instant;

public record ConsumeLoginAssertionResult(
        String jti,
        boolean success,
        ConsumeLoginAssertionReason reason,
        Instant consumedAt
) {
}