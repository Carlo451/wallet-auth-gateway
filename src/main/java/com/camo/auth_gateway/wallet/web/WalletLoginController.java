package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.dto.authrequestobj.OpenId4VpAuthorizationRequest;
import com.camo.auth_gateway.wallet.service.WalletCallbackService;
import com.camo.auth_gateway.wallet.service.WalletFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletLoginController {
    private final WalletFlowService walletFlowService;
    private final WalletCallbackService walletCallbackService;

    @PostMapping("/login/start")
    public StartWalletLoginResponse startLogin(@RequestBody StartWalletLoginRequest request) {
        return walletFlowService.startLogin(request);
    }

    @GetMapping("/request/{sessionId}")
    public String getRequestObject(@PathVariable UUID sessionId) {
        return walletFlowService.getRequestObject(sessionId);
    }

    @GetMapping("/login/{sessionId}/status")
    public WalletStatusResponse getStatus(@PathVariable UUID sessionId) {
        return walletFlowService.getStatus(sessionId);
    }

    @PostMapping(
            value = "/callback",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public void callback(@RequestParam MultiValueMap<String, String> formData) {
        walletCallbackService.handleCallback(formData);
    }
}
