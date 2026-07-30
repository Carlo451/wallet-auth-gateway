package com.camo.auth_gateway.wallet.dto.heidi;

public record DecodedDisclosure(String salt, String claimName, String claimValue, String digest) {

}
