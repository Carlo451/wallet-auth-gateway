package com.camo.auth_gateway.identity.repository;

import com.camo.auth_gateway.identity.domain.RegistrationSession;
import com.camo.auth_gateway.identity.domain.RegistrationSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationSessionRepository extends JpaRepository<RegistrationSession, Long> {

    Optional<RegistrationSession> findBySessionId(String sessionId);
    Optional<RegistrationSession> findByState(String state);

    List<RegistrationSession> findByStatusAndExpiresAtBefore(
            RegistrationSessionStatus status,
            Instant timestamp
    );
}
