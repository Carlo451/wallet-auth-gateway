package com.camo.auth_gateway.identity.api;

import com.camo.auth_gateway.wallet.dto.heidi.DecodedDisclosure;

public interface IdentityCreationApi {
    String buildIdentityLink(String givenName, String familyName, String birthPlace, String birthDate);
}
