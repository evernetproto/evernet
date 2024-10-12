package org.evernet.core.exception;

public class AuthenticationException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Authentication failed";
    }
}