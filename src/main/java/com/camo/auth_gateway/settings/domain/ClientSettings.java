package com.camo.auth_gateway.settings.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "client_settings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_client_settings_client_id", columnNames = "client_id"),
                @UniqueConstraint(name = "uk_client_settings_base_url", columnNames = "base_url")
        }
)
public class ClientSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false, unique = true, length = 100)
    private String clientId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "base_url", length = 500)
    private String baseUrl;

    @Column(name = "login_endpoint", length = 500)
    private String loginEndpoint;

    @Column(name = "registration_endpoint", length = 500)
    private String registrationEndpoint;

    @Column(name = "success_redirect_uri", length = 500)
    private String successRedirectUri;

    @Column(name = "error_redirect_uri", length = 500)
    private String errorRedirectUri;

    @Column(name = "audience", length = 255)
    private String audience;


    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AskedClaimForRegister> askedClaimsForRegister = new ArrayList<>();

    public ClientSettings() {
    }

    public ClientSettings(String clientId,
                          String name,
                          String description,
                          boolean enabled,
                          String baseUrl,
                          String loginEndpoint,
                          String registrationEndpoint,
                          String successRedirectUri,
                          String errorRedirectUri,
                          String audience,
                          List<String> requestedClaims) {
        this.clientId = clientId;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.baseUrl = baseUrl;
        this.loginEndpoint = loginEndpoint;
        this.registrationEndpoint = registrationEndpoint;
        this.successRedirectUri = successRedirectUri;
        this.errorRedirectUri = errorRedirectUri;
        this.audience = audience;

    }

    public Long getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getLoginEndpoint() {
        return loginEndpoint;
    }

    public String getRegistrationEndpoint() {
        return registrationEndpoint;
    }

    public String getSuccessRedirectUri() {
        return successRedirectUri;
    }

    public String getErrorRedirectUri() {
        return errorRedirectUri;
    }

    public String getAudience() {
        return audience;
    }




    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setLoginEndpoint(String loginEndpoint) {
        this.loginEndpoint = loginEndpoint;
    }

    public void setRegistrationEndpoint(String registrationEndpoint) {
        this.registrationEndpoint = registrationEndpoint;
    }

    public void setSuccessRedirectUri(String successRedirectUri) {
        this.successRedirectUri = successRedirectUri;
    }

    public void setErrorRedirectUri(String errorRedirectUri) {
        this.errorRedirectUri = errorRedirectUri;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<AskedClaimForRegister> getAskedClaimsForRegister() {
        return askedClaimsForRegister;
    }

    public void setAskedClaimsForRegister(List<AskedClaimForRegister> askedClaimsForRegister) {
        this.askedClaimsForRegister = askedClaimsForRegister;
    }

    public void addAskedClaimForRegister(AskedClaimForRegister claim) {
        askedClaimsForRegister.add(claim);
        claim.setSettings(this);
    }

    public void removeAskedClaimForRegister(AskedClaimForRegister claim) {
        askedClaimsForRegister.remove(claim);
        claim.setSettings(null);
    }
}
