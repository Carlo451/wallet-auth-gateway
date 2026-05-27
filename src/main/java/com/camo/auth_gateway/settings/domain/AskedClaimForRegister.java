package com.camo.auth_gateway.settings.domain;

import jakarta.persistence.*;

@Entity
public class AskedClaimForRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String claimName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_settings_id", nullable = false)
    private ClientSettings settings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaimName() {
        return claimName;
    }

    public void setClaimName(String claimName) {
        this.claimName = claimName;
    }

    public ClientSettings getSettings() {
        return settings;
    }

    public void setSettings(ClientSettings settings) {
        this.settings = settings;
    }
}
