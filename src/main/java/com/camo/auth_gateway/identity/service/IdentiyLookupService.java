package com.camo.auth_gateway.identity.service;

import com.camo.auth_gateway.identity.api.IdentityLookupApi;
import com.camo.auth_gateway.identity.api.model.PseudonymMappingDto;
import com.camo.auth_gateway.identity.domain.PseudonymMapping;
import com.camo.auth_gateway.identity.domain.PseudonymMappingStatus;
import com.camo.auth_gateway.identity.repository.PseudonymMappingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdentiyLookupService implements IdentityLookupApi {

    private final PseudonymMappingRepository pseudonymMappingRepository;

    public IdentiyLookupService(PseudonymMappingRepository repository) {
        this.pseudonymMappingRepository = repository;
    }
    @Override
    public Optional<PseudonymMappingDto> findActiveMapping(String clientId, String pseudonymValue) {
        Optional<PseudonymMapping> mapping = pseudonymMappingRepository.findByClientIdAndPseudonymValueAndStatus(clientId,pseudonymValue, PseudonymMappingStatus.ACTIVE);
        if (mapping.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(PseudonymMappingDto.from(mapping.get()));
    }

    @Override
    public boolean checkIfPseudonymMappingExists(String clientId, String pseudonymValue) {
        return pseudonymMappingRepository.existsByClientIdAndPseudonymValue(clientId,pseudonymValue);
    }

}
