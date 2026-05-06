package com.camo.auth_gateway.backendbridge.http;

import com.camo.auth_gateway.backendbridge.api.dto.BackendRegistrationFailedException;
import com.camo.auth_gateway.backendbridge.http.dto.RegistrationAssertionRequest;
import com.camo.auth_gateway.backendbridge.http.dto.RegistrationBackendResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class RegistrationBackendClient {

    private final RestClient restClient;

    public RegistrationBackendClient(RestClient restClientBuilder) {
        this.restClient = restClientBuilder;
    }

    public RegistrationBackendResponse register(String registrationEndpoint,
                                                RegistrationAssertionRequest request) {
        return restClient.post()
                .uri(registrationEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (clientRequest, clientResponse) -> {
                    throw new BackendRegistrationFailedException(
                            "Backend registration failed with status " + clientResponse.getStatusCode()
                    );
                })
                .body(RegistrationBackendResponse.class);
    }
}