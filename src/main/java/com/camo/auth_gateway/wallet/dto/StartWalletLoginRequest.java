package com.camo.auth_gateway.wallet.dto;

import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StartWalletLoginRequest(
        String redirectUri,

        @NotBlank
        String clientId,

        @NotNull
        WalletFlowType flowType

){}
