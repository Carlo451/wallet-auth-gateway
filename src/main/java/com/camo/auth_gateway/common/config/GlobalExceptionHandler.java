package com.camo.auth_gateway.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public String handleException(Exception e,
                                  HttpServletRequest request,
                                  Model model) {
        Object sessionId = request.getAttribute("sessionId");

        if (sessionId != null) {
            model.addAttribute("sessionId", sessionId == null? "sessionId" : sessionId.toString());
        }

        model.addAttribute("errorMessage",
                e.getMessage() != null ? e.getMessage() : "Unbekannter Fehler");
        model.addAttribute("stacktrace",
                ExceptionUtils.getStackTrace(e));
        //return "wallet_error";
        return "redirect:/wallet/error";
    }
}
