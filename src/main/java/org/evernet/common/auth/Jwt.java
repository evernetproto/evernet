package org.evernet.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";
    private static final String TOKEN_TYPE_ACTOR = "ACTOR";

    @Value("${evernet.vertex.jwt-secret-key}")
    private String jwtSecretKey;

    @Value("${evernet.vertex.endpoint}")
    private String vertex;

    public AuthenticatedAdmin getAdmin(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)))
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .requireAudience(vertex)
                .requireIssuer(vertex)
                .build()
                .parseSignedClaims(token).getPayload();

        return AuthenticatedAdmin.builder()
                .identifier(claims.getSubject())
                .build();
    }

    public AuthenticatedActor getActor(String token) {
        return null;
    }

    public String getAdminToken(AuthenticatedAdmin admin) {
        return Jwts.builder().subject(admin.getIdentifier())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .issuer(vertex)
                .audience().add(vertex)
                .and()
                .signWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String getActorToken(ActorReference actor, PrivateKey privateKey, NodeReference targetNode) {
        if (Objects.isNull(targetNode)) {
            targetNode = actor.getNode();
        }

        return Jwts.builder().subject(actor.getAddress())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACTOR)
                .issuer(actor.getNode().getAddress())
                .audience().add(targetNode.getAddress())
                .and()
                .header().keyId(actor.getNode().getAddress())
                .and()
                .signWith(privateKey)
                .compact();
    }
}