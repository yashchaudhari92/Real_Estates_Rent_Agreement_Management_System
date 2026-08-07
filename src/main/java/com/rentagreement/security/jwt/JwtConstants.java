package com.rentagreement.security.jwt;

public class JwtConstants {

    private JwtConstants() {
    }

    public static final String SECRET_KEY =
            "RentAgreementManagementSystemSecretKeyForJwtAuthentication2026";

    public static final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24; // 24 Hours

}