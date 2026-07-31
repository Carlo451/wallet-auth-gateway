package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionResult;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.WalletAuthContinueResponse;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import com.camo.auth_gateway.wallet.service.WalletContinueService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
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

    /// Handles the redirect logic for a login or a registration
    /// @param sessionId identification
    /// @return a redirect to the external application login endpoint or to the /wallet/registration-success endpoint
    @GetMapping("/{sessionId}")
    public String successPage(@PathVariable UUID sessionId, HttpServletRequest request) throws IllegalArgumentException {
        request.setAttribute("sessionId",sessionId);
        WalletSession session = walletSessionRepository.findById(sessionId).orElseThrow(()-> new IllegalArgumentException("session not found"));

        if (session.getFlowType().equals(WalletFlowType.REGISTRATION)) {
            walletContinueService.startRegistration(session);
            return "redirect:/wallet/registration-success/{sessionId}";
        } else {
            WalletAuthContinueResponse result = walletContinueService.createLoginAssertion(session);
            return "redirect:" + result.redirectExternalBackendUri();
        }

    }
}
