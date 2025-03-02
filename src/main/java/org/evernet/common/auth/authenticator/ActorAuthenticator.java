package org.evernet.common.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.evernet.common.auth.*;
import org.evernet.common.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ActorAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Override
    public void authenticate(HttpServletRequest request) {
        String jwtToken = AuthUtils.extractToken(request);

        AuthenticatedActor actor = jwt.getActor(jwtToken);

        if (Objects.isNull(actor)) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setActor(actor);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedActorController.class;
    }
}