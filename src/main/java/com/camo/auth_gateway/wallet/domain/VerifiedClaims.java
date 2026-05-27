package com.camo.auth_gateway.wallet.domain;

import com.camo.auth_gateway.wallet.dto.heidi.DecodedDisclosure;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class VerifiedClaims {
    List<VerifiedDisclosure> claims = new ArrayList<>();

    public List<VerifiedDisclosure> getClaims() {
        return claims;
    }

    public void setClaims(List<VerifiedDisclosure> claims) {
        this.claims = claims;
    }
}