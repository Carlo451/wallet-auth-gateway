package com.camo.auth_gateway.wallet.repository;

import com.camo.auth_gateway.wallet.domain.WalletSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletSessionRepository extends JpaRepository<WalletSession, UUID> {
    @Query
    Optional<WalletSession> findByState(@Param("state") String state);

    long deleteByExpiresAtBefore(Instant threshold);
}
