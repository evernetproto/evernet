package org.evernet.core.auth;

public class AuthenticatedAdminController {

    public AuthenticatedAdmin getAdmin() {
        return ThreadLocalWrapper.getAdmin();
    }

    public String getAdminIdentifier() {
        return getAdmin().getIdentifier();
    }
}