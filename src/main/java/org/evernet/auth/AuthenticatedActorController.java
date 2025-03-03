package org.evernet.auth;

import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.evernet.common.exception.NotAllowedException;

public class AuthenticatedActorController {

    public AuthenticatedActor getActor() {
        return ThreadLocalWrapper.getActor();
    }

    public ActorReference getActorReference() {
        return getActor().getActor();
    }

    public NodeReference getTargetNode() {
        return getActor().getTargetNode();
    }

    public Boolean isLocalActor() {
        return getActor().isLocal();
    }

    public void checkLocalActor() {
        if (!isLocalActor()) {
            throw new NotAllowedException();
        }
    }
}
