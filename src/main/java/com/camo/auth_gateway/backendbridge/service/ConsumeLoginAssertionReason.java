package com.camo.auth_gateway.backendbridge.service;

public enum ConsumeLoginAssertionReason {
    CONSUMED,
    ALREADY_CONSUMED,
    EXPIRED,
    NOT_FOUND,
    CLIENT_MISMATCH,
    ASSERTION_TYPE_MISMATCH
}