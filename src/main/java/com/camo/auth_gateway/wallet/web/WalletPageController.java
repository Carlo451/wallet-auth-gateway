package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.settings.api.exceptions.SettingsNotFoundException;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.dto.WalletRegSuccessResponse;
import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.service.WalletFlowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

    /// Provides the wallet page with the qr code for a specific session
    /// @param sessionId identification of session
    /// @param model
    /// @return wallet-page resource
    @GetMapping("/wallet/page/{sessionId}")
    public String walletPage(@PathVariable UUID sessionId, Model model) {
        WalletStatusResponse status = walletFlowService.getStatus(sessionId);

        model.addAttribute("sessionId", sessionId);
        model.addAttribute("status", status.status());
        model.addAttribute("flowType", status.flowType().toString());
        model.addAttribute("openid4vpUrl",walletFlowService.getOpenid4vpUrl(sessionId));

        return "wallet-page";
    }

    /// is the redirect starting point to initiate new session for auth
    /// @param clientId the client identification from client settings
    /// @param flowType the flow type, login or registration
    /// @param redirectUri the redirectURI, can be specified also in client settings
    /// @param redirectAttributes
    /// @redirect to /wallet/page/{sessionId} or /wallet/error
    @GetMapping("/wallet/page/auth")
    public String walletAuthStartPage(@RequestParam(required = true)  String clientId,
                                      @RequestParam(required = true) String flowType,
                                      @RequestParam(required = false) String redirectUri,
                                      RedirectAttributes redirectAttributes
                                      ) {
        WalletFlowType flowTypeEnum;
        try {
            flowTypeEnum = WalletFlowType.valueOf(flowType);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage","The WAG was not able to read the flowType");
            return "redirect:/wallet/error";
        }
        try {
            StartWalletLoginRequest request = new StartWalletLoginRequest(redirectUri,clientId,flowTypeEnum);
            StartWalletLoginResponse sessionStart = walletFlowService.startAuth(request);
            return "redirect:/wallet/page/"+sessionStart.sessionId();
        } catch(Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
            return "redirect:/wallet/error";
        }
    }


    /// Provides the status of a auth session
    /// @param sessionId identifiaction of the session
    /// @return WalletStatusResponse with basic informations of session
    @GetMapping("/wallet/status/{sessionId}")
    @ResponseBody
    public WalletStatusResponse walletStatus(@PathVariable UUID sessionId, HttpServletRequest request) {
        request.setAttribute("sessionId",sessionId);
        return walletFlowService.getStatus(sessionId);
    }

    /// Provides the error page for a specific session
    /// @param sessionId path parameter for identification of the session
    /// @return wallet-error resource
    @GetMapping("/wallet/error/{sessionId}")
    public String walletErrorWithSessionId(Model model, @PathVariable UUID sessionId) {
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("stacktrace","Not yet");
        model.addAttribute("errorMessage","Something went wrong");

        return "wallet-error";
    }

    /// Provides the error page for a specific session
    /// @return wallet-error-basic resource
    @GetMapping("/wallet/error")
    public String walletError(Model model) {
        return "wallet-error-basic";
    }

    /// Provides the resource for the successfull registration
    /// @param sessionId identification of session
    /// @param model
    /// @return registration-success resource
    @GetMapping("/wallet/registration-success/{sessionId}")
    public String registrationSuccess(@PathVariable UUID sessionId, Model model,HttpServletRequest request) throws IllegalArgumentException, SettingsNotFoundException {
        //if an error is thrown, the session id is in the request and can be handled inside the exception handler
        request.setAttribute("sessionId",sessionId);

        WalletRegSuccessResponse res = walletFlowService.getSuccessfullRegResponse(sessionId);
        model.addAttribute("redirectUrl", res.redirectUrl());
        model.addAttribute("sessionId", sessionId);
        return "registration-success";
    }

}
