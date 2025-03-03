package org.evernet.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.evernet.common.exception.InvalidTokenException;
import org.evernet.common.exception.ServerException;
import org.evernet.common.util.Ed25519;
import org.evernet.node.service.NodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
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

    private final NodeService nodeService;

    private final RemoteNodeService remoteNodeService;

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
        Jws<Claims> signature = Jwts.parser()
                .keyLocator(header -> {
                    String keyId = (String) header.get("kid");
                    NodeReference nodeReference = NodeReference.from(keyId);
                    if (nodeReference.getVertex().equals(vertex)) {
                        // local key id
                        try {
                            return Ed25519.stringToPublicKey(nodeService.get(nodeReference.getIdentifier()).getSigningPublicKey());
                        } catch (Exception e) {
                            throw new ServerException(e.getMessage());
                        }
                    } else {
                        // remote key id
                        try {
                            return Ed25519.stringToPublicKey(remoteNodeService.get(nodeReference.getVertex(), nodeReference.getIdentifier()).getSigningPublicKey());
                        } catch (Exception e) {
                            throw new ServerException(e.getMessage());
                        }
                    }
                })
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACTOR)
                .build()
                .parseSignedClaims(token);

        Claims claims = signature.getPayload();
        JwsHeader header = signature.getHeader();
        String subject = claims.getSubject();
        ActorReference subjectActor = ActorReference.from(subject);

        // Check kid and sub match
        if (!header.getKeyId().equals(subjectActor.getNode().getAddress())) {
            throw new InvalidTokenException();
        }

        // check sub and issuer match
        if (!claims.getIssuer().equals(subjectActor.getNode().getAddress())) {
            throw new InvalidTokenException();
        }

        // check kid and issuer match
        if (!header.getKeyId().equals(claims.getIssuer())) {
            throw new InvalidTokenException();
        }

        // check aud vertex and vertex match
        Optional<String> audience = claims.getAudience().stream().findFirst();
        if (audience.isEmpty()) {
            throw new InvalidTokenException();
        }

        NodeReference audienceNode = NodeReference.from(audience.get());
        if (!audienceNode.getVertex().equals(vertex)) {
            throw new InvalidTokenException();
        }

        // check aud node exists
        if (!nodeService.identifierExists(audienceNode.getIdentifier())) {
            throw new InvalidTokenException();
        }

        return AuthenticatedActor.builder().actor(subjectActor).targetNode(audienceNode).build();
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