package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.settings.api.exceptions.SettingsNotFoundException;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.dto.WalletRegSuccessResponse;
import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.service.WalletFlowService;

import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.UUID;

@WebMvcTest(WalletPageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WalletPageControllerTests {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    WalletFlowService walletFlowService;


    @Test
    void startNormalLoginAuth() throws Exception {
        UUID id = UUID.randomUUID();
        StartWalletLoginResponse response = new StartWalletLoginResponse(id,"status","requestUri","openId4VpUrl", Instant.now().plusSeconds(10));
        Mockito.when(walletFlowService.startAuth(any(StartWalletLoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/page/auth")
                        .param("clientId", "client-1")
                        .param("flowType", "LOGIN")
                        .param("redirectUri", "https://example.com/callback"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/page/"+id));
    }

    @Test
    void startNormalRegAuth() throws Exception {
        UUID id = UUID.randomUUID();
        StartWalletLoginResponse response = new StartWalletLoginResponse(id,"status","requestUri","openId4VpUrl", Instant.now().plusSeconds(10));
        Mockito.when(walletFlowService.startAuth(any(StartWalletLoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/page/auth")
                        .param("clientId", "client-1")
                        .param("flowType", "REGISTRATION")
                        .param("redirectUri", "https://example.com/callback"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/page/"+id));
    }

    @Test
    void startNormalAuth_WithWrongFlowType() throws Exception {
        UUID id = UUID.randomUUID();
        StartWalletLoginResponse response = new StartWalletLoginResponse(id,"status","requestUri","openId4VpUrl", Instant.now().plusSeconds(10));
        Mockito.when(walletFlowService.startAuth(any(StartWalletLoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/page/auth")
                        .param("clientId", "client-1")
                        .param("flowType", "WRONG_FLOW_TYPE")
                        .param("redirectUri", "https://example.com/callback"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/error"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("errorMessage"));
    }


    @Test
    void showWalletPage() throws Exception {
        UUID id = UUID.randomUUID();
        WalletStatusResponse response = new WalletStatusResponse(id,"status",false,false,  WalletFlowType.LOGIN);
        Mockito.when(walletFlowService.getStatus(any(UUID.class)))
                .thenReturn(response);
        Mockito.when(walletFlowService.getOpenid4vpUrl(any(UUID.class)))
                .thenReturn("openid4vpUrl");

        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/page/"+id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void showWalletPage_ErrorInGetStatus() throws Exception {
        UUID id = UUID.randomUUID();
        WalletStatusResponse response = new WalletStatusResponse(id,"status",false,false,  WalletFlowType.LOGIN);
        Mockito.when(walletFlowService.getStatus(any(UUID.class)))
                .thenThrow(new IllegalArgumentException("Session not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/page/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/error"));
    }

    @Test
    void showRegistrationSuccessPage() throws Exception {
        UUID id = UUID.randomUUID();
        WalletRegSuccessResponse response = new WalletRegSuccessResponse("https://redirectUri.com");
        Mockito.when(walletFlowService.getSuccessfullRegResponse(any(UUID.class)))
                .thenReturn(response);


        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/registration-success/"+id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void showRegistrationSuccessPage_NoSessionId() throws Exception {
        UUID id = UUID.randomUUID();
        WalletRegSuccessResponse response = new WalletRegSuccessResponse("https://redirectUri.com");
        Mockito.when(walletFlowService.getSuccessfullRegResponse(any(UUID.class)))
                .thenThrow(new IllegalArgumentException("Does not found session."));


        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/registration-success/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/error/"+id));
    }

    @Test
    void showRegistrationSuccessPage_SettingsForClientIdCannotBeFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(walletFlowService.getSuccessfullRegResponse(any(UUID.class)))
                .thenThrow(new SettingsNotFoundException("Does not found settings."));


        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/registration-success/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/error"));
    }

    @Test
    void showWalletError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/error"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void showWalletError_ForSpecificSessionId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/error/"+UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getWalletStatus() throws Exception {
        UUID id = UUID.randomUUID();
        WalletStatusResponse response = new WalletStatusResponse(id,"status",true,true,WalletFlowType.LOGIN);
        Mockito.when(walletFlowService.getStatus(any(UUID.class)))
                .thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/status/"+UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getWalletStatus_IdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        WalletStatusResponse response = new WalletStatusResponse(id,"status",true,true,WalletFlowType.LOGIN);
        Mockito.when(walletFlowService.getStatus(any(UUID.class)))
                .thenThrow(new IllegalArgumentException("Session not found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/status/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/error/"+id));

    }

}
