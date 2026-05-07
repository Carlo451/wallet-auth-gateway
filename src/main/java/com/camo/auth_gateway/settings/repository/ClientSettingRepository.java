package com.camo.auth_gateway.settings.repository;

import com.camo.auth_gateway.settings.domain.ClientSettings;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientSettingRepository extends JpaRepository<ClientSettings, Long> {

    Optional<ClientSettings> findByClientId(String clientId);
    Optional<ClientSettings> findByBaseUrl(String baseUrl);
}

