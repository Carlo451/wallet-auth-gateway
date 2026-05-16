package com.camo.auth_gateway.wallet.api;

import org.springframework.util.MultiValueMap;

public interface HandleWalletLoginCallbackUseCase {
    void handleCallback(MultiValueMap<String, String> formData);
}
