package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.service.WalletFlowService;

import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

}
