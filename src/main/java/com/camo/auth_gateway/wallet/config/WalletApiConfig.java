package com.camo.auth_gateway.wallet.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "gateway.api")
@NullMarked
@Validated
public record WalletApiConfig(
        @NotNull
        OpenId4VPConfig openId4vp,
        @NotBlank
        @Pattern(regexp = "^/.*$", message = "controller-path must start with '/'")
        String controllerPath) {

}
