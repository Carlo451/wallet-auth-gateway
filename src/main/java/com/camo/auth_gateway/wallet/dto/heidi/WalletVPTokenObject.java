package com.camo.auth_gateway.wallet.dto.heidi;

import com.nimbusds.jose.JOSEException;

import java.text.ParseException;
import java.util.List;

public interface WalletVPTokenObject {
    public List<DecodedDisclosure> getDecodedClaims();
    public DecodedDisclosure findDisclosureObjectForClaimName(String claimName);
    public boolean verifyKeyBinding() throws ParseException, JOSEException;
    boolean verifiyDisclosures() throws ParseException;
}
