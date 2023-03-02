package org.evernet.identity.authenticator;

import org.evernet.identity.model.KnownEntity;
import org.evernet.identity.pojo.EntityDetails;

public class AuthenticatedController implements RequiresAuthentication {

    public EntityDetails getAuthenticatedEntity() {
        return ThreadLocalWrapper.getAuthenticatedEntity();
    }

    public KnownEntity getKnownEntity() {
        return ThreadLocalWrapper.getKnownEntity();
    }

    public String getKnownEntityId() {
        return getKnownEntity().getId();
    }

    public String getPublicKey() {
        return getKnownEntity().getPublicKey();
    }
}
