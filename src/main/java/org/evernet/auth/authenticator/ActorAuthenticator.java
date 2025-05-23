package org.evernet.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.auth.Jwt;
import org.evernet.auth.ThreadLocalWrapper;
import org.evernet.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActorAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Override
    public void authenticate(HttpServletRequest request) {
        String token = AuthUtils.extractToken(request);

        AuthenticatedActor actor = jwt.getActor(token);

        if (actor == null) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setActor(actor);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedActorController.class;
    }
}
