package com.camo.auth_gateway.wallet.dto;

import com.camo.auth_gateway.wallet.domain.WalletFlowType;

import java.util.UUID;

public record WalletStatusResponse(
        UUID sessionId,
        String status,
        boolean verified,
        boolean expired,
        WalletFlowType flowType
) {
}