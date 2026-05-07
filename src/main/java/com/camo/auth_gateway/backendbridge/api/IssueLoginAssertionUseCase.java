package com.camo.auth_gateway.backendbridge.api;

import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionResult;
import com.camo.auth_gateway.backendbridge.api.dto.IssueLoginAssertionCommand;

public interface IssueLoginAssertionUseCase {
    IssueLoginAssertionResult createLoginAssertion(IssueLoginAssertionCommand command);
}
