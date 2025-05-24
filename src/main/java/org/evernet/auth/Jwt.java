package org.evernet.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.evernet.bean.ActorAddress;
import org.evernet.bean.NodeAddress;
import org.evernet.exception.InvalidTokenException;
import org.evernet.exception.ServerException;
import org.evernet.service.ConfigService;
import org.evernet.service.NodeKeyService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";
    private static final String TOKEN_TYPE_ACTOR = "ACTOR";
    private static final String TOKEN_TYPE_NODE = "NODE";

    private final ConfigService configService;

    private final NodeKeyService nodeKeyService;

    public AuthenticatedAdmin getAdmin(String token) {
        String vertexEndpoint = configService.getVertexEndpoint();
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(configService.getJwtSigningKey().getBytes(StandardCharsets.UTF_8)))
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .requireAudience(vertexEndpoint)
                .requireIssuer(vertexEndpoint)
                .build()
                .parseSignedClaims(token).getPayload();

        return AuthenticatedAdmin.builder()
                .identifier(claims.getSubject())
                .build();
    }

    public String getAdminToken(AuthenticatedAdmin admin) {
        String vertexEndpoint = configService.getVertexEndpoint();

        return Jwts.builder().subject(admin.getIdentifier())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .issuer(vertexEndpoint)
                .audience().add(vertexEndpoint)
                .and()
                .signWith(Keys.hmacShaKeyFor(configService.getJwtSigningKey().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public AuthenticatedActor getActor(String token) {
        Jws<Claims> claims = Jwts.parser()
                .keyLocator(header -> {
                    String keyId = header.get("kid").toString();
                    try {
                        return nodeKeyService.getSigningPublicKey(keyId);
                    } catch (GeneralSecurityException e) {
                        throw new ServerException(e.getMessage());
                    }
                })
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACTOR)
                .build()
                .parseSignedClaims(token);

        Claims payload = claims.getPayload();
        JwsHeader headers = claims.getHeader();

        String issuer = payload.getIssuer();
        NodeAddress issuerNode = NodeAddress.fromString(issuer);

        if (!headers.getKeyId().equals(issuer)) {
            throw new InvalidTokenException();
        }

        Set<String> audience = payload.getAudience();
        if (audience.size() != 1) {
            throw new InvalidTokenException();
        }

        String aud = audience.iterator().next();
        NodeAddress audienceNode = NodeAddress.fromString(aud);

        String vertexEndpoint = configService.getVertexEndpoint();
        if (!vertexEndpoint.equals(audienceNode.getVertexEndpoint())) {
            throw new InvalidTokenException();
        }

        String subject = payload.getSubject();
        return AuthenticatedActor.builder()
                .actorIdentifier(subject)
                .actorNodeIdentifier(issuerNode.getNodeIdentifier())
                .actorNodeAddress(issuer)
                .actorAddress(ActorAddress.builder().nodeAddress(issuerNode).actorIdentifier(subject).build().toString())
                .actorVertexEndpoint(issuerNode.getVertexEndpoint())
                .targetNodeAddress(aud)
                .targetNodeIdentifier(audienceNode.getNodeIdentifier())
                .targetVertexEndpoint(audienceNode.getVertexEndpoint())
                .build();
    }

    public String getActorToken(String actorIdentifier, String actorNodeIdentifier, String targetNodeAddress, PrivateKey privateKey) {
        String vertexEndpoint = configService.getVertexEndpoint();

        if (targetNodeAddress == null) {
            targetNodeAddress = NodeAddress.builder().vertexEndpoint(vertexEndpoint).nodeIdentifier(actorNodeIdentifier).build().toString();
        }

        NodeAddress actorNodeAddress = NodeAddress.builder().vertexEndpoint(vertexEndpoint).nodeIdentifier(actorNodeIdentifier).build();

        return Jwts.builder().subject(actorIdentifier)
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACTOR)
                .issuer(actorNodeAddress.toString())
                .audience().add(targetNodeAddress)
                .and()
                .header().keyId(actorNodeAddress.toString())
                .and()
                .signWith(privateKey)
                .compact();
    }

    public String getNodeToken(String nodeIdentifier, String vertexEndpoint, String targetNodeAddress, PrivateKey privateKey) {
        NodeAddress nodeAddress = NodeAddress.builder().vertexEndpoint(vertexEndpoint).nodeIdentifier(nodeIdentifier).build();

        return Jwts.builder().subject(nodeIdentifier)
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_NODE)
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .issuer(nodeAddress.toString())
                .audience().add(targetNodeAddress)
                .and()
                .header().keyId(nodeAddress.toString())
                .and()
                .signWith(privateKey)
                .compact();
    }

    public AuthenticatedNode getNode(String token) {
        Jws<Claims> claims = Jwts.parser()
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_NODE)
                .keyLocator(header -> {
                    String keyId = header.get("kid").toString();
                    try {
                        return nodeKeyService.getSigningPublicKey(keyId);
                    } catch (GeneralSecurityException e) {
                        throw new ServerException(e.getMessage());
                    }
                })
                .build()
                .parseSignedClaims(token);

        JwsHeader headers = claims.getHeader();
        Claims payload = claims.getPayload();

        String issuer = payload.getIssuer();
        NodeAddress issuerNode = NodeAddress.fromString(issuer);

        if (!headers.getKeyId().equals(issuer)) {
            throw new InvalidTokenException();
        }

        String subject = payload.getSubject();

        if (!issuerNode.getNodeIdentifier().equals(subject)) {
            throw new InvalidTokenException();
        }

        Set<String> audience = payload.getAudience();
        if (audience.size() != 1) {
            throw new InvalidTokenException();
        }

        String aud = audience.iterator().next();
        NodeAddress audienceNode = NodeAddress.fromString(aud);

        String vertexEndpoint = configService.getVertexEndpoint();
        if (!vertexEndpoint.equals(audienceNode.getVertexEndpoint())) {
            throw new InvalidTokenException();
        }

        return AuthenticatedNode.builder()
                .nodeIdentifier(subject)
                .nodeVertexEndpoint(issuerNode.getVertexEndpoint())
                .nodeAddress(issuerNode.toString())
                .targetNodeAddress(aud)
                .targetNodeIdentifier(audienceNode.getNodeIdentifier())
                .targetVertexEndpoint(audienceNode.getVertexEndpoint())
                .build();
    }
}
