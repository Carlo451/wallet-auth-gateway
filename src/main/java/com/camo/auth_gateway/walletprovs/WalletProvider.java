package com.camo.auth_gateway.walletprovs;

import com.camo.auth_gateway.wallet.domain.WalletFlowType;
import com.camo.auth_gateway.wallet.domain.WalletSession;
import com.camo.auth_gateway.wallet.dto.heidi.WalletVPTokenObject;
import com.nimbusds.jwt.JWTClaimsSet;

public interface WalletProvider<T extends WalletVPTokenObject> {
    JWTClaimsSet buildJWTClaimsSet(WalletFlowType flowType, WalletSession session) throws Exception;
    String buildWalletIdFromWalletDisclosures(T jwtClaimsSet) throws Exception;
}
