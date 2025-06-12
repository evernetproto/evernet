package org.evernet.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedUser;
import org.evernet.auth.AuthenticatedUserController;
import org.evernet.auth.Jwt;
import org.evernet.auth.ThreadLocalWrapper;
import org.evernet.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Override
    public void authenticate(HttpServletRequest request) {
        String token = AuthUtils.extractToken(request);

        AuthenticatedUser user = jwt.getUser(token);

        if (user == null) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setUser(user);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedUserController.class;
    }
}