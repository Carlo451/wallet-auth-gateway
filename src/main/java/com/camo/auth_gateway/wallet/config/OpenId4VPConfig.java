package com.camo.auth_gateway.wallet.config;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OpenId4VPConfig(
        @NotBlank
        @Pattern(regexp = "^/.*$", message = "request-object-path must start with '/'")
        String requestObjectPath,
        @NotBlank
        @Pattern(regexp = "^/.*$", message = "callback-path must start with '/'")
        String callbackPath) {
}
