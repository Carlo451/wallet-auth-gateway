package com.camo.auth_gateway.wallet.service;

import com.camo.auth_gateway.wallet.repository.WalletSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletSessionCleanupService {
    private final WalletSessionRepository walletSessionRepository;

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void cleanupExpiredSessions() {
        long deletedCount = walletSessionRepository.deleteByExpiresAtBefore(Instant.now());

        if (deletedCount > 0) {
            log.info("Deleted {} expired wallet sessions by scheduled job.", deletedCount);
        }
    }
}
