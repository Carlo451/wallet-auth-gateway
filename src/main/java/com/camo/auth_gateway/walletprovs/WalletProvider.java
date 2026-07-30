package com.camo.auth_gateway.walletprovs;

import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.heidi.WalletVPTokenObject;
import com.nimbusds.jwt.JWTClaimsSet;

public interface WalletProvider<T extends WalletVPTokenObject> {
    /// builds the JWT claim set for the OpenId4VP request object
    /// @param flowType if login or registration
    /// @param session the complete session obj
    /// @return JWTClaimsSet the request obj
    /// @throws Exception
    JWTClaimsSet buildJWTClaimsSet(WalletFlowType flowType, WalletSession session) throws Exception;

    /// Retrieves the attributes for the wallet identity hash and calls the identity creation
    /// @param jwtClaimsSet an wallet specific token object
    /// @return String the identity hash
    /// @throws Exception
    String buildWalletIdFromWalletDisclosures(T jwtClaimsSet) throws Exception;
}
