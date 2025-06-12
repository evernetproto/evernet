package org.evernet.auth;

import org.evernet.exception.NotAllowedException;

public class AuthenticatedUserController {

    public AuthenticatedUser getUser() {
        return ThreadLocalWrapper.getUser();
    }

    public String getUserIdentifier() {
        return getUser().getUserIdentifier();
    }

    public String getUserAddress() {
        return getUser().getUserAddress();
    }

    public String getUserNodeIdentifier() {
        return getUser().getUserNodeIdentifier();
    }

    public String getUserNodeAddress() {
        return getUser().getUserNodeAddress();
    }

    public String getUserVertexEndpoint() {
        return getUser().getUserVertexEndpoint();
    }

    public String getTargetNodeIdentifier() {
        return getUser().getTargetNodeIdentifier();
    }

    public String getTargetNodeAddress() {
        return getUser().getTargetNodeAddress();
    }

    public String getTargetVertexEndpoint() {
        return getUser().getTargetVertexEndpoint();
    }

    public void checkLocal() {
        if (!getTargetNodeAddress().equals(getUserNodeAddress())) {
            throw new NotAllowedException();
        }
    }
}