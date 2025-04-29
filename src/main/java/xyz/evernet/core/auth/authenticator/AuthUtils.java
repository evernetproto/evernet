package xyz.evernet.core.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import xyz.evernet.core.exception.InvalidTokenException;

import java.util.Objects;

@UtilityClass
public class AuthUtils {

    public static String extractToken(HttpServletRequest request) {
        return extractCredentials(request, "Bearer");
    }

    private static String extractCredentials(HttpServletRequest request, String type) {
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
