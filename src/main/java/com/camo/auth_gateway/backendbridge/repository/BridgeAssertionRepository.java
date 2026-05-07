package com.camo.auth_gateway.backendbridge.repository;

import com.camo.auth_gateway.backendbridge.domain.BridgeAssertionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BridgeAssertionRepository extends JpaRepository<BridgeAssertionEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BridgeAssertionEntity> findByJtiAndClientId(String Jti, String clientId);
}
