package com.rentagreement.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.hmacShaKeyFor(
            JwtConstants.SECRET_KEY.getBytes()
    );

    public String generateToken(String username, String role) {

        return Jwts.builder()

                .setSubject(username)

                .claim("role", role)

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis() +
                                        JwtConstants.EXPIRATION_TIME
                        )
                )

                .signWith(key, SignatureAlgorithm.HS256)

                .compact();
    }

    public String extractUsername(String token) {

        return extractClaims(token).getSubject();

    }

    public String extractRole(String token) {

        return extractClaims(token)
                .get("role", String.class);

    }

    public boolean isTokenValid(String token) {

        try {

            extractClaims(token);

            return true;

        } catch (Exception e) {

            return false;

        }

    }

    private Claims extractClaims(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(key)

                .build()

                .parseClaimsJws(token)

                .getBody();

    }

}