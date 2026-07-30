package com.camo.auth_gateway.identity.api;

import com.camo.auth_gateway.wallet.dto.heidi.DecodedDisclosure;

public interface IdentityCreationApi {
    /// Creates the identity hash of a wallet
    /// @param givenName the given name attribute of the wallet presentation
    /// @param familyName the family name attribute of the wallet presentation
    /// @param birthPlace the birthplace attribute of the wallet presentation
    /// @param birthDate the birthdate attribute of the wallet presentation
    /// @return String the hash of the wallet
    String buildIdentityLink(String givenName, String familyName, String birthPlace, String birthDate);
}
