package com.camo.auth_gateway.backendbridge.api.dto;

import java.time.Instant;

public record IssueLoginAssertionResult(
        String jti,
        Instant expiresAt,
        String signedAssertion
) {
}