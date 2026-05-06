package com.camo.auth_gateway.wallet.dto;

public record StartWalletLoginRequest(
        String purpose,
        String redirectUri
){}
