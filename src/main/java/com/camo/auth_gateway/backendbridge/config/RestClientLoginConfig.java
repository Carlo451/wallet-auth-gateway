package com.camo.auth_gateway.backendbridge.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;


@Configuration
public class RestClientLoginConfig {

    private final BridgeExternalApiCalls bridgeExternalApiCalls;

    public RestClientLoginConfig(BridgeExternalApiCalls bridgeExternalApiCalls) {
        this.bridgeExternalApiCalls = bridgeExternalApiCalls;
    }

    @Bean
    @Qualifier("loginRestClient")
    RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(bridgeExternalApiCalls.loginReadTimoutSeconds()));

        return RestClient.builder().requestFactory(requestFactory).build();
    }
}
