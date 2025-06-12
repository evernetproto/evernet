package org.evernet.auth;

public class ThreadLocalWrapper {

    private static final ThreadLocal<AuthenticatedAdmin> adminContext;

    private static final ThreadLocal<AuthenticatedUser> userContext;

    private static final ThreadLocal<AuthenticatedNode> nodeContext;

    static {
        adminContext = new ThreadLocal<>();
        userContext = new ThreadLocal<>();
        nodeContext = new ThreadLocal<>();
    }

    public static AuthenticatedAdmin getAdmin() {
        return adminContext.get();
    }

    public static void setAdmin(AuthenticatedAdmin admin) {
        adminContext.set(admin);
    }

    public static AuthenticatedUser getUser() {
        return userContext.get();
    }

    public static void setUser(AuthenticatedUser user) {
        userContext.set(user);
    }

    public static AuthenticatedNode getNode() {
        return nodeContext.get();
    }

    public static void setNode(AuthenticatedNode node) {
        nodeContext.set(node);
    }
}