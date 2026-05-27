package com.camo.auth_gateway.wallet.dto.heidi;

import java.util.List;

public interface WalletVPTokenObject {
    public List<DecodedDisclosure> getDecodedClaims();
    public DecodedDisclosure findDisclosureObjectForClaimName(String claimName);
}
