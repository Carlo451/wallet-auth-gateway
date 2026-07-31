package com.camo.auth_gateway.wallet.web;

import com.camo.auth_gateway.backendbridge.api.dto.BackendRegistrationFailedException;
import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionResult;
import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.WalletAuthContinueResponse;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import com.camo.auth_gateway.wallet.service.WalletContinueService;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(WalletContinueController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WalletContinueControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    WalletContinueService walletContinueService;

    @MockitoBean
    WalletSessionRepository walletSessionRepository;

    @Test
    void redircetToExternalBackendToLogin() throws Exception {
        UUID id = UUID.randomUUID();
        WalletSession session = Mockito.mock(WalletSession.class);
        Mockito.when(session.getFlowType()).thenReturn(WalletFlowType.LOGIN);
        Mockito.when(session.getId()).thenReturn(id);
        Mockito.when(walletSessionRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(session));
        IssueLoginAssertionResult result = new IssueLoginAssertionResult("jti", Instant.now(),"signedAssertion");
        WalletAuthContinueResponse response = new WalletAuthContinueResponse(result,"https://redirect-login-endpoint.com/loginEndpoint/assertion");
        Mockito.when(walletContinueService.createLoginAssertion(session)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/auth/continue/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("https://redirect-login-endpoint.com/loginEndpoint/assertion"));
    }

    @Test
    void makeWalletContinueCall_SessionNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(walletSessionRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/auth/continue/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/error/"+id));
    }

    @Test
    void tryContinueLogin_SettingsForClientIdNotAvailable() throws Exception {
        UUID id = UUID.randomUUID();
        WalletSession session = Mockito.mock(WalletSession.class);
        Mockito.when(session.getFlowType()).thenReturn(WalletFlowType.LOGIN);
        Mockito.when(session.getId()).thenReturn(id);
        Mockito.when(walletSessionRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(session));
        Mockito.when(walletContinueService.createLoginAssertion(session)).thenThrow(new IllegalArgumentException("Can not find session"));
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/auth/continue/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/error/"+id));
    }

    @Test
    void makeRegistrationWalletContinue() throws Exception {
        UUID id = UUID.randomUUID();
        WalletSession session = Mockito.mock(WalletSession.class);
        Mockito.when(session.getFlowType()).thenReturn(WalletFlowType.REGISTRATION);
        Mockito.when(session.getId()).thenReturn(id);
        Mockito.when(walletSessionRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(session));
        Mockito.when(walletContinueService.startRegistration(session)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/auth/continue/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/registration-success/"+id));
    }
    @Test
    void makeRegistrationWalletContinue_RegistrationFailed() throws Exception {
        UUID id = UUID.randomUUID();
        WalletSession session = Mockito.mock(WalletSession.class);
        Mockito.when(session.getFlowType()).thenReturn(WalletFlowType.REGISTRATION);
        Mockito.when(session.getId()).thenReturn(id);
        Mockito.when(walletSessionRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(session));
        Mockito.when(walletContinueService.startRegistration(session)).thenThrow(new BackendRegistrationFailedException("Registration failed"));
        mockMvc.perform(MockMvcRequestBuilders.get("/wallet/auth/continue/"+id))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wallet/error/"+id));
    }
}
