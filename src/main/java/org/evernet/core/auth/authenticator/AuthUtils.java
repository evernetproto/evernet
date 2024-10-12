package org.evernet.core.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.evernet.core.exception.InvalidTokenException;

import java.util.Objects;

@UtilityClass
public class AuthUtils {

    private static final String CREDENTIAL_BEARER_TOKEN = "Bearer";

    public String extractToken(HttpServletRequest request) {
        return extractCredential(request, CREDENTIAL_BEARER_TOKEN);
    }

    private String extractCredential(HttpServletRequest request, String type) {
        String authorizationHeader = request.getHeader("Authorization");

        if (Objects.isNull(authorizationHeader)) {
            throw new InvalidTokenException();
        }

        String[] authorizationComponents = authorizationHeader.split("\\s+");

        if (authorizationComponents.length != 2) {
            throw new InvalidTokenException();
        }

        if (!authorizationComponents[0].equals(type)) {
            throw new InvalidTokenException();
        }

        return authorizationComponents[1];
    }
}