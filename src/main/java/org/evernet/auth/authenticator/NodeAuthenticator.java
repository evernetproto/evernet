package org.evernet.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.*;
import org.evernet.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NodeAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Override
    public void authenticate(HttpServletRequest request) {
        String token = AuthUtils.extractToken(request);

        AuthenticatedNode node = jwt.getNode(token);

        if (node == null) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setNode(node);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedNodeController.class;
    }
}
