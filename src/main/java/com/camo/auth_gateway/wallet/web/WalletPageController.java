package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.service.WalletCallbackService;
import com.camo.auth_gateway.wallet.service.WalletFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WalletPageController {
    private final WalletFlowService walletFlowService;
    private final WalletCallbackService walletCallbackService;

    @GetMapping("/wallet/page/{sessionId}")
    public String walletPage(@PathVariable UUID sessionId, Model model) {
        WalletStatusResponse status = walletFlowService.getStatus(sessionId);

        model.addAttribute("sessionId", sessionId);
        model.addAttribute("status", status.status());
        model.addAttribute("openid4vpUrl",walletFlowService.getOpenid4vpUrl(sessionId));

        return "wallet-page";
    }
    @GetMapping("/wallet/status/{sessionId}")
    @ResponseBody
    public WalletStatusResponse walletStatus(@PathVariable UUID sessionId) {
        return walletFlowService.getStatus(sessionId);
    }

    @GetMapping("/wallet/error/{sessionId}")
    @ResponseBody
    public WalletStatusResponse walletError(@PathVariable UUID sessionId) {
        return walletFlowService.getStatus(sessionId);
    }

    @GetMapping("/wallet/continue/{sessionId}")
    public String continueAfterWallet(@PathVariable UUID sessionId, Model model) {
        //WalletCallbackResult result = walletCallbackService.handleCallback(sessionId);

        //model.addAttribute("assertion", result.signedAssertion());
        //model.addAttribute("externalLoginUrl", result.externalLoginUrl());

        return "bridge-login-handoff";
    }
}
