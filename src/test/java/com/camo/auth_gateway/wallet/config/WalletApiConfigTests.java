package com.camo.auth_gateway.wallet.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.xml.bind.ValidationException;
import org.aspectj.apache.bcel.classfile.Module;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.method.MethodValidationException;

import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties(WalletApiConfig.class)
public class WalletApiConfigTests {
    @Autowired
    WalletApiConfig apiConfig;

    @Test
    void testAutoMappingOfWalletApiConfig() {
        Assertions.assertThat(apiConfig.controllerPath()).isEqualTo("/test/wallet/api");
        OpenId4VPConfig openId4VPConfig = apiConfig.openId4vp();
        Assertions.assertThat(openId4VPConfig.callbackPath()).isEqualTo("/test/openid4vp/callback");
        Assertions.assertThat(openId4VPConfig.requestObjectPath()).isEqualTo("/test/openid4vp/request");
    }

    @Test
    void testValidationOfWalletApiConfig_Success() {
        OpenId4VPConfig openId4VPConfig = new OpenId4VPConfig("/requestObjectPath","/callbackPath");
        WalletApiConfig config = new WalletApiConfig(openId4VPConfig,"/controllerPath");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<WalletApiConfig>> violations = validator.validate(config);
        Assertions.assertThat(violations).isEmpty();
    }


    @Test
    void testValidationOfWalletApiConfig_OpenId4VPPropsAreNull() {
        OpenId4VPConfig openId4VPConfig = null;
        WalletApiConfig config = new WalletApiConfig(openId4VPConfig,"/controllerPath");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<WalletApiConfig>> violations = validator.validate(config);
        Assertions.assertThat(violations).isNotEmpty();
    }
    @Test
    void testValidationOfWalletApiConfig_BasePathDoesNotStartWithDash() {
        OpenId4VPConfig openId4VPConfig = new OpenId4VPConfig("/requestObjectPath","/callbackPath");
        WalletApiConfig config = new WalletApiConfig(openId4VPConfig,"controllerPath");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<WalletApiConfig>> violations = validator.validate(config);
        Assertions.assertThat(violations).isNotEmpty();
    }
}
