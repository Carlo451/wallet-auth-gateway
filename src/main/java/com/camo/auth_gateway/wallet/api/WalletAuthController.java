package com.camo.auth_gateway.wallet.api;

import com.camo.auth_gateway.wallet.dto.OpenId4VPRequestObject;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.service.WalletCallbackService;
import com.camo.auth_gateway.wallet.service.WalletFlowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${gateway.api.controller-path}")
@RequiredArgsConstructor
public class WalletAuthController {
    private final WalletFlowService walletFlowService;
    private final WalletCallbackService walletCallbackService;

    @PostMapping("/auth/start")
    public StartWalletLoginResponse startLogin(@RequestBody @Valid StartWalletLoginRequest request) {
        return walletFlowService.startAuth(request);
    }

    @GetMapping("${gateway.api.openId4vp.request-object-path}/{sessionId}")
    public OpenId4VPRequestObject getRequestObject(@PathVariable UUID sessionId) {
        return walletFlowService.getRequestObject(sessionId);
    }

    @GetMapping("/auth/{sessionId}/status")
    public WalletStatusResponse getStatus(@PathVariable UUID sessionId) {
        return walletFlowService.getStatus(sessionId);
    }

    @PostMapping(
            value = "${gateway.api.openId4vp.callback-path}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public void callback(@RequestParam MultiValueMap<String, String> formData) throws Exception {
        walletCallbackService.handleCallback(formData);
    }
}
