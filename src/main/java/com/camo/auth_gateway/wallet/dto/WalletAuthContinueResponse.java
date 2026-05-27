package com.camo.auth_gateway.wallet.dto;

import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionResult;

public record WalletAuthContinueResponse(IssueLoginAssertionResult result, String redirectExternalBackendUri) {

}
