package com.camo.auth_gateway.wallet.dto;

import java.time.Instant;
import java.util.UUID;

public record StartWalletLoginResponse(
        UUID sessionId,
        String status,
        String requestUri,
        String openid4vpUrl,
        Instant expiresAt
) {
}