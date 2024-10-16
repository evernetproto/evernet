package org.evernet.core.auth;

public class AuthenticatedActorController {

    public AuthenticatedActor getActor() {
        return ThreadLocalWrapper.getActor();
    }

    public String getActorIdentifier() {
        return getActor().getIdentifier();
    }

    public String getActorAddress() {
        return getActor().getAddress();
    }

    public String getSourceNodeIdentifier() {
        return getActor().getSourceNodeIdentifier();
    }

    public String getSourceVertex() {
        return getActor().getSourceVertex();
    }

    public String getTargetNodeIdentifier() {
        return getActor().getTargetNodeIdentifier();
    }

    public String getTargetVertex() {
        return getActor().getTargetVertex();
    }

    public String getSourceNodeAddress() {
        return getActor().getSourceNodeAddress();
    }

    public String getTargetNodeAddress() {
        return getActor().getTargetNodeAddress();
    }

    public Boolean isLocalActor() {
        return getActor().isLocal();
    }
}