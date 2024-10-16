package org.evernet.core.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.evernet.core.exception.InvalidTokenException;
import org.evernet.core.exception.ServerException;
import org.evernet.core.util.Ed25519KeyPairUtil;
import org.evernet.node.service.NodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String CLAIM_TOKEN_TYPE = "type";
    private static final String TOKEN_TYPE_ADMIN = "admin";
    private static final String TOKEN_TYPE_ACTOR = "actor";
    private static final String HEADER_KEY_ID = "kid";

    @Value("${evernet.jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${evernet.vertex}")
    private String vertex;

    private final NodeService nodeService;

    public AuthenticatedAdmin getAdmin(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)))
                .require(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ADMIN)
                .requireAudience(vertex)
                .requireIssuer(vertex)
                .build()
                .parseSignedClaims(token).getPayload();

        return AuthenticatedAdmin.builder()
                .identifier(claims.getSubject())
                .build();
    }

    public String getAdminToken(AuthenticatedAdmin admin) {
        return Jwts.builder().subject(admin.getIdentifier())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ADMIN)
                .issuer(vertex)
                .audience().add(vertex)
                .and()
                .signWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public AuthenticatedActor getActor(String token) {
        Jws<Claims> claims = Jwts.parser()
                .keyLocator(header -> {
                    String keyId = header.get(HEADER_KEY_ID).toString();
                    String[] sourceComponents = keyId.split("/");
                    String sourceVertex = sourceComponents[0];
                    String sourceNodeIdentifier = sourceComponents[1];

                    if (sourceVertex.equals(vertex)) {
                        try {
                            return Ed25519KeyPairUtil.stringToPublicKey(nodeService.get(sourceNodeIdentifier).getSigningPublicKey());
                        } catch (Exception e) {
                            throw new ServerException(e.getMessage());
                        }
                    } else {
                        throw new ServerException("Not implemented");
                    }
                })
                .require(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ADMIN)
                .build()
                .parseSignedClaims(token);

        return getValidatedActorFromClaims(claims);
    }

    public String getActorToken(AuthenticatedActor actor, PrivateKey privateKey) {
        return Jwts.builder()
                .subject(actor.getAddress())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACTOR)
                .issuer(actor.getSourceNodeAddress())
                .audience().add(actor.getTargetNodeAddress())
                .and()
                .header().keyId(actor.getSourceNodeAddress())
                .and()
                .signWith(privateKey)
                .compact();
    }

    private AuthenticatedActor getValidatedActorFromClaims(Jws<Claims> claims) {
        Claims payload = claims.getPayload();
        Header header = claims.getHeader();

        Optional<String> targetNodeAddressOptional = payload.getAudience().stream().findFirst();
        if (targetNodeAddressOptional.isEmpty()) {
            throw new InvalidTokenException();
        }

        String actorAddress = payload.getSubject();
        String[] actorComponents = actorAddress.split("/");
        if (actorComponents.length != 3) {
            throw new InvalidTokenException();
        }
        String actorVertex = actorComponents[0];
        String actorNodeIdentifier = actorComponents[1];
        String actorIdentifier = actorComponents[2];

        String targetNodeAddress = targetNodeAddressOptional.get();
        String[] targetNodeComponents = targetNodeAddress.split("/");
        if (targetNodeComponents.length != 2) {
            throw new InvalidTokenException();
        }
        String targetNodeIdentifier = targetNodeComponents[1];
        String targetNodeVertex = targetNodeComponents[0];
        if (!targetNodeVertex.equals(vertex)) {
            throw new InvalidTokenException();
        }
        if (!nodeService.exists(targetNodeIdentifier)) {
            throw new InvalidTokenException();
        }

        String sourceNodeAddress = payload.getIssuer();
        String[] sourceNodeComponents = sourceNodeAddress.split("/");
        if (sourceNodeComponents.length != 2) {
            throw new InvalidTokenException();
        }
        String sourceNodeIdentifier = sourceNodeComponents[1];
        String sourceNodeVertex = sourceNodeComponents[0];
        if (!sourceNodeVertex.equals(actorVertex) || !sourceNodeIdentifier.equals(actorNodeIdentifier)) {
            throw new InvalidTokenException();
        }
        if (!sourceNodeAddress.equals(header.get(HEADER_KEY_ID).toString())) {
            throw new InvalidTokenException();
        }

        return AuthenticatedActor.builder()
                .identifier(actorIdentifier)
                .targetVertex(targetNodeVertex)
                .targetNodeIdentifier(targetNodeIdentifier)
                .sourceVertex(sourceNodeVertex)
                .sourceNodeIdentifier(sourceNodeIdentifier)
                .build();
    }
}
