package org.evernet.identity.authenticator;

import org.evernet.identity.model.KnownEntity;
import org.evernet.identity.pojo.EntityDetails;

public class ThreadLocalWrapper {

    private static final ThreadLocal<EntityDetails> AUTHENTICATED_ENTITY_THREAD_LOCAL;

    private static final ThreadLocal<KnownEntity> KNOWN_ENTITY_THREAD_LOCAL;

    static {
        AUTHENTICATED_ENTITY_THREAD_LOCAL = new ThreadLocal<>();
        KNOWN_ENTITY_THREAD_LOCAL = new ThreadLocal<>();
    }

    public static void setAuthenticatedEntity(EntityDetails entity) {
        AUTHENTICATED_ENTITY_THREAD_LOCAL.set(entity);
    }

    public static EntityDetails getAuthenticatedEntity() {
        return AUTHENTICATED_ENTITY_THREAD_LOCAL.get();
    }

    public static void setKnownEntity(KnownEntity entity) {
        KNOWN_ENTITY_THREAD_LOCAL.set(entity);
    }

    public static KnownEntity getKnownEntity() {
        return KNOWN_ENTITY_THREAD_LOCAL.get();
    }
}
