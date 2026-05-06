package com.camo.auth_gateway.identity.repository;

import com.camo.auth_gateway.identity.domain.PseudonymMapping;
import com.camo.auth_gateway.identity.domain.PseudonymMappingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PseudonymMappingRepository extends JpaRepository<PseudonymMapping, Long> {

    Optional<PseudonymMapping> findByClientIdAndPseudonymValueAndStatus(
            String clientId,
            String pseudonymValue,
            PseudonymMappingStatus status
    );

    boolean existsByClientIdAndPseudonymValue(String clientId, String pseudonymValue);

}
