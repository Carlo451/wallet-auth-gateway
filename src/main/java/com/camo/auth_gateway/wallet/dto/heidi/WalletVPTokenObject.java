package com.camo.auth_gateway.wallet.dto.heidi;

import com.nimbusds.jose.JOSEException;

import java.text.ParseException;
import java.util.List;

public interface WalletVPTokenObject {
    /// gets all decoded claim objects inside the token object
    /// @return List<DecodedDisclosure>
    public List<DecodedDisclosure> getDecodedClaims();

    /// Finds the Disclosure object for the given claim name
    /// @param claimName
    /// @return DecodedDisclosure
    public DecodedDisclosure findDisclosureObjectForClaimName(String claimName);

    /// Verifies the key binding of the OpenId4VP VP Token
    /// @return boolean
    /// @throws ParseException
    /// @throws JOSEException
    public boolean verifyKeyBinding() throws ParseException, JOSEException;

    /// Verifies all disclosures inside the VP Token object
    /// @return boolean
    /// @throws ParseException
    boolean verifiyDisclosures() throws ParseException;
}
