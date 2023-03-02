package org.evernet.identity.service;

import org.evernet.identity.model.Entity;
import org.evernet.identity.pojo.EntityDetails;
import org.evernet.identity.pojo.PublicKeyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

@Service
public class EntityFacadeService {

    private final EntityService entityService;

    private final RemoteEntityService remoteEntityService;

    private final String nodeEndpoint;

    @Autowired
    public EntityFacadeService(EntityService entityService, RemoteEntityService remoteEntityService,
                               @Value("${evernet.node.endpoint}") String nodeEndpoint) throws NoSuchAlgorithmException {
        this.entityService = entityService;
        this.remoteEntityService = remoteEntityService;
        this.nodeEndpoint = nodeEndpoint;
    }

    public EntityDetails fetch(String node, String identifier) throws Throwable {
        if (node.equals(nodeEndpoint)) {
            return fetchLocalEntity(node, identifier);
        } else {
            return fetchRemoteEntity(node, identifier);
        }
    }

    private EntityDetails fetchRemoteEntity(String node, String identifier) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Entity remoteEntity = remoteEntityService.fetchPublicKey(node, identifier);
        return constructAuthenticatedEntity(remoteEntity, node, identifier);
    }

    private EntityDetails fetchLocalEntity(String node, String identifier) throws Throwable {
        Entity localEntity = entityService.get(identifier);
        return constructAuthenticatedEntity(localEntity, node, identifier);
    }

    private EntityDetails constructAuthenticatedEntity(Entity entity, String node, String identifier) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PublicKeyData publicKeyData = new PublicKeyData(entity.getPublicKey(), node, identifier);
        return new EntityDetails(entity, publicKeyData, node);
    }
}
