package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionResult;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.WalletAuthContinueResponse;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import com.camo.auth_gateway.wallet.service.WalletContinueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/wallet/auth/continue")
@RequiredArgsConstructor
public class WalletContinueController {

    private final WalletContinueService walletContinueService;
    private final WalletSessionRepository walletSessionRepository;

    @GetMapping("/{sessionId}")
    public String successPage(@PathVariable UUID sessionId, Model model) {
        WalletSession session = walletSessionRepository.findById(sessionId).orElseThrow(()-> new RuntimeException("session not found"));

        if (session.getFlowType().equals(WalletFlowType.REGISTRATION)) {
            walletContinueService.startRegistration(session);
        } else {
            WalletAuthContinueResponse result = walletContinueService.createLoginAssertion(session);
            model.addAttribute("sessionId", sessionId);
            return "redirect:" + result.redirectExternalBackendUri()+"/"+result.result().signedAssertion();
        }
        return null;

    }
}
