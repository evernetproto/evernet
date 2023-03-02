package org.evernet.identity.authenticator;

import io.jsonwebtoken.*;
import org.evernet.common.exception.InvalidTokenException;
import org.evernet.identity.pojo.EntityDetails;
import org.evernet.identity.service.EntityFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class TokenParser {

    private final EntityFacadeService entityFacadeService;

    @Autowired
    public TokenParser(EntityFacadeService entityFacadeService) {
        this.entityFacadeService = entityFacadeService;
    }

    public Token parse(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKeyResolver(new SigningKeyResolver() {
            @Override
            public Key resolveSigningKey(JwsHeader header, Claims claims) {
                try {
                    return resolveKey(header.getKeyId());
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Key resolveSigningKey(JwsHeader header, String plaintext) {
                try {
                    return resolveKey(header.getKeyId());
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }).parseClaimsJws(token);

        Claims c = claims.getBody();

        String identifier = c.getSubject();
        String audience = c.getAudience();

        return new Token(audience, identifier);
    }

    private Key resolveKey(String keyId) throws Throwable {
        String[] keyIdComponents = keyId.split("@");

        if (keyIdComponents.length != 2) {
            throw new InvalidTokenException();
        }

        String identifier = keyIdComponents[0];
        String node = keyIdComponents[1];

        EntityDetails entityDetails = entityFacadeService.fetch(node, identifier);
        ThreadLocalWrapper.setAuthenticatedEntity(entityDetails);

        return entityDetails.getPublicKey().getObject();
    }
}
