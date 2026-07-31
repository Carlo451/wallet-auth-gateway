package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.wallet.config.OpenId4VPConfig;
import com.camo.auth_gateway.wallet.config.WalletApiConfig;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.service.WalletCallbackService;
import com.camo.auth_gateway.wallet.service.WalletFlowService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
@WebMvcTest(WalletAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
//@EnableConfigurationProperties(WalletApiConfig.class)
@ActiveProfiles("test")
public class WalletAuthControllerTests {
    @Autowired
    MockMvc mockMvc;


    @MockitoBean
    WalletFlowService walletFlowService;

    @MockitoBean
    WalletCallbackService walletCallbackService;

    @Test
    void startWalletSession_Test() throws Exception {
        StartWalletLoginResponse res = new StartWalletLoginResponse(UUID.randomUUID(),"status","requestUri","url", Instant.now());
        StartWalletLoginRequest request = new StartWalletLoginRequest("redirectUri","clientId", WalletFlowType.LOGIN);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);
        Mockito.when(walletFlowService.startAuth(any(StartWalletLoginRequest.class))).thenReturn(res);
        mockMvc.perform(MockMvcRequestBuilders.post("test/wallet/api/auth/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
    @Test
    void startWalletSession_NoBody_Test() throws Exception {
        StartWalletLoginResponse res = new StartWalletLoginResponse(UUID.randomUUID(),"status","requestUri","url", Instant.now());
        Mockito.when(walletFlowService.startAuth(any(StartWalletLoginRequest.class))).thenReturn(res);
        mockMvc.perform(MockMvcRequestBuilders.post("test/wallet/api/auth/start"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void startWalletSession_WrongBody_Test() throws Exception {
        StartWalletLoginResponse res = new StartWalletLoginResponse(UUID.randomUUID(),"status","requestUri","url", Instant.now());
        Map<String,String> request = Map.of("key","value");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);
        Mockito.when(walletFlowService.startAuth(any(StartWalletLoginRequest.class))).thenReturn(res);
        mockMvc.perform(MockMvcRequestBuilders.post("test/wallet/api/auth/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    }
}
