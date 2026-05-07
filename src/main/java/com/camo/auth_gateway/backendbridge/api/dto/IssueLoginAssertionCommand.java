package com.camo.auth_gateway.backendbridge.api.dto;

public record IssueLoginAssertionCommand(
        String clientId,
        String subject,
        String linkedAccountId
) {
}
