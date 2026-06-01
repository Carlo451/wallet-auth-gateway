package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.dto.WalletRegSuccessResponse;
import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.service.WalletCallbackService;
import com.camo.auth_gateway.wallet.service.WalletFlowService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("flowType", status.flowType().toString());
        model.addAttribute("openid4vpUrl",walletFlowService.getOpenid4vpUrl(sessionId));

        return "wallet-page";
    }

    @GetMapping("/wallet/page/auth")
    public String walletAuthStartPage(@RequestParam(required = true)  String clientId,
                                      @RequestParam(required = true) WalletFlowType flowType,
                                      @RequestParam(required = false)  String purpose,
                                      @RequestParam(required = false) String redirectUri

                                      ) {
        if (flowType == WalletFlowType.LOGIN) {
            StartWalletLoginRequest request = new StartWalletLoginRequest(purpose,redirectUri,clientId,flowType);
            StartWalletLoginResponse sessionStart = walletFlowService.startLogin(request);
            return "redirect:/wallet/page/"+sessionStart.sessionId();
        } else if (flowType == WalletFlowType.REGISTRATION) {
            StartWalletLoginRequest request = new StartWalletLoginRequest(purpose,redirectUri,clientId,flowType);
            StartWalletLoginResponse sessionStart = walletFlowService.startLogin(request);
            return "redirect:/wallet/page/"+sessionStart.sessionId();
        }
        throw new IllegalArgumentException("Invalid flow type");
    }


    @GetMapping("/wallet/status/{sessionId}")
    @ResponseBody
    public WalletStatusResponse walletStatus(@PathVariable UUID sessionId) {
        return walletFlowService.getStatus(sessionId);
    }

    @GetMapping("/wallet/error/{sessionId}")
    public String walletError(Model model, @PathVariable UUID sessionId) {
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("stacktrace","Not yet");
        model.addAttribute("errorMessage","Something went wrong");

        return "wallet-error";
    }

    @GetMapping("/wallet/registration-success/{sessionId}")
    public String registrationSuccess(@PathVariable UUID sessionId, Model model) {
        WalletRegSuccessResponse res = walletFlowService.getSuccessfullRegResponse(sessionId);
        model.addAttribute("redirectUrl", res.redirectUrl());
        model.addAttribute("sessionId", sessionId);
        return "registration-success";
    }

    /*@GetMapping("/wallet/continue/{sessionId}")
    public String continueAfterWallet(@PathVariable UUID sessionId, Model model) {
        return "bridge-login-handoff";
    }*/

}
