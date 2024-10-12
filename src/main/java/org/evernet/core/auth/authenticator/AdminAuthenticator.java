package org.evernet.core.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedAdmin;
import org.evernet.core.auth.AuthenticatedAdminController;
import org.evernet.core.auth.Jwt;
import org.evernet.core.auth.ThreadLocalWrapper;
import org.evernet.core.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AdminAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Value("${evernet.vertex}")
    private String vertex;

    @Override
    public void authenticate(HttpServletRequest request) {
        String jwtToken = AuthUtils.extractToken(request);

        AuthenticatedAdmin admin = jwt.getAdmin(jwtToken, vertex, vertex);

        if (Objects.isNull(admin)) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setAdmin(admin);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedAdminController.class;
    }
}
