package org.evernet.auth;

import org.evernet.exception.NotAllowedException;

public class AuthenticatedActorController {

    public AuthenticatedActor getActor() {
        return ThreadLocalWrapper.getActor();
    }

    public String getActorIdentifier() {
        return getActor().getActorIdentifier();
    }

    public String getActorAddress() {
        return getActor().getActorAddress();
    }

    public String getActorNodeIdentifier() {
        return getActor().getActorNodeIdentifier();
    }

    public String getActorNodeAddress() {
        return getActor().getActorNodeAddress();
    }

    public String getActorVertexEndpoint() {
        return getActor().getActorVertexEndpoint();
    }

    public String getTargetNodeIdentifier() {
        return getActor().getTargetNodeIdentifier();
    }

    public String getTargetNodeAddress() {
        return getActor().getTargetNodeAddress();
    }

    public String getTargetVertexEndpoint() {
        return getActor().getTargetVertexEndpoint();
    }

    public void checkLocal() {
        if (!getTargetNodeAddress().equals(getActorNodeAddress())) {
            throw new NotAllowedException();
        }
    }
}
