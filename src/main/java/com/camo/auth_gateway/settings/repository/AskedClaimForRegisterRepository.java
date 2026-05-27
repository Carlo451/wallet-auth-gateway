package com.camo.auth_gateway.settings.repository;

import com.camo.auth_gateway.settings.domain.AskedClaimForRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AskedClaimForRegisterRepository extends JpaRepository<AskedClaimForRegister, Long> {
}