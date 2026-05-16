package com.camo.auth_gateway.wallet.dto;

import com.camo.auth_gateway.wallet.domain.WalletFlowType;

public record StartWalletLoginRequest(
        String purpose,
        String redirectUri,
        String clientId,
        WalletFlowType flowType

){}
