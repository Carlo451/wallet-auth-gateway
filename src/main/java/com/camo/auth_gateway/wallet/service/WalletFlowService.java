package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.wallet.domain.WalletFlowState;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginRequest;
import com.camo.auth_gateway.wallet.dto.StartWalletLoginResponse;
import com.camo.auth_gateway.wallet.dto.WalletStatusResponse;
import com.camo.auth_gateway.wallet.dto.authrequestobj.*;
import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletFlowService {
    @Value("${gateway.app.network.baseurl}")
    private String baseUrl;
    private final WalletSessionRepository walletSessionRepository;

    public StartWalletLoginResponse startLogin(StartWalletLoginRequest request) {
        Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);

        WalletSession session = WalletSession.createNew(
                request.redirectUri(),
                expiresAt
        );

        session.markPending();
        walletSessionRepository.save(session);

        String requestUri = "http://localhost:8081/api/wallet/request/" + session.getId();
        String openid4vpUrl = "openid4vp://authorize?request_uri_method=post&request_uri=" + requestUri;

        return new StartWalletLoginResponse(
                session.getId(),
                session.getFlowState().name(),
                requestUri,
                openid4vpUrl,
                session.getExpiresAt()
        );
    }

    public OpenId4VpAuthorizationRequest getRequestObject(UUID sessionId) {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));

        OpenId4VpAuthorizationRequest request = new OpenId4VpAuthorizationRequest();

        request.setClientId(baseUrl);
        request.setResponseType("vp_token");
        request.setResponseMode("direct_post");
        request.setResponseUri(baseUrl+"/api/wallet/callback");
        request.setNonce(session.getNonce());
        request.setState(session.getState());

        DcqlClaimQuery givenNameClaim = new DcqlClaimQuery();
        givenNameClaim.setId("given_name");
        givenNameClaim.setPath(List.of("given_name"));

        DcqlClaimQuery birthdateClaim = new DcqlClaimQuery();
        birthdateClaim.setId("birthdate");
        birthdateClaim.setPath(List.of("birth_date")); // je nach Credential-Schema evtl. "birthdate"

        DcqlCredentialMeta meta = new DcqlCredentialMeta();
        meta.setVctValues(List.of("eu.europa.ec.eudi:pid.1"));

        DcqlCredentialQuery credentialQuery = new DcqlCredentialQuery();
        credentialQuery.setId("pid");
        credentialQuery.setFormat("dc+sd-jwt");
        credentialQuery.setMeta(meta);
        credentialQuery.setClaims(List.of(givenNameClaim, birthdateClaim));

        DcqlQuery dcqlQuery = new DcqlQuery();
        dcqlQuery.setCredentials(List.of(credentialQuery));

        request.setDcqlQuery(dcqlQuery);

        return request;
    }

    public String getOpenid4vpUrl(UUID sessionId) {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));

        String requestUri = baseUrl+"/api/wallet/request/" + session.getId();
        return "openid4vp://authorize?request_uri=" + requestUri;
    }

    public WalletStatusResponse getStatus(UUID sessionId) {
        WalletSession session = walletSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet session not found"));

        boolean expired = session.isExpired();
        WalletFlowState state = session.getFlowState();

        if (expired && state != WalletFlowState.VERIFIED) {
            state = WalletFlowState.EXPIRED;
        }

        return new WalletStatusResponse(
                session.getId(),
                state.name(),
                state == WalletFlowState.VERIFIED,
                expired
        );
    }
}
