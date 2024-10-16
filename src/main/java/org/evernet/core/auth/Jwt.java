package org.evernet.core.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ADMIN = "admin";
    private static final String TOKEN_TYPE_ACTOR = "actor";

    @Value("${evernet.jwt.secret.key}")
    private String jwtSecretKey;

    public AuthenticatedAdmin getAdmin(String token, String audience, String issuer) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)))
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .requireAudience(audience)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token).getPayload();

        return AuthenticatedAdmin.builder()
                .identifier(claims.getSubject())
                .build();
    }

    public String getAdminToken(AuthenticatedAdmin admin, String issuer, String audience) {
        return Jwts.builder().subject(admin.getIdentifier())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .issuer(issuer)
                .audience().add(audience)
                .and()
                .signWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String getActorToken(AuthenticatedActor actor, PrivateKey privateKey) {
        return Jwts.builder()
                .subject(actor.getAddress())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACTOR)
                .issuer(actor.getSourceNodeAddress())
                .audience().add(actor.getTargetNodeAddress())
                .and()
                .header().keyId(actor.getSourceNodeAddress())
                .and()
                .signWith(privateKey)
                .compact();
    }
}
