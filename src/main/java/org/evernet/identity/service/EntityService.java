package org.evernet.identity.service;

import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.identity.model.Entity;
import org.evernet.identity.pojo.EntityRegistrationRequest;
import org.evernet.identity.pojo.EntityUpdateRequest;
import org.evernet.identity.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Supplier;

@Service
public class EntityService {

    private final EntityRepository entityRepository;

    @Autowired
    public EntityService(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public Entity register(EntityRegistrationRequest entityRegistrationRequest) {
        if (identifierExists(entityRegistrationRequest.getIdentifier())) {
            throw new ClientException("Entity " + entityRegistrationRequest.getIdentifier() + " already exists");
        }

        if (publicKeyExists(entityRegistrationRequest.getPublicKey())) {
            throw new ClientException("Public key already exists");
        }

        Entity entity = new Entity(entityRegistrationRequest.getType(),
                entityRegistrationRequest.getIdentifier(),
                entityRegistrationRequest.getDisplayName(),
                entityRegistrationRequest.getDescription(),
                entityRegistrationRequest.getEncryptedPrivateKey(),
                entityRegistrationRequest.getPublicKey());

        return entityRepository.save(entity);
    }

    public Entity get(String identifier) throws Throwable {
        return entityRepository.findByIdentifier(identifier)
                .orElseThrow((Supplier<Throwable>) () -> new NotFoundException("Entity " + identifier + " not found"));
    }

    public Entity getByPublicKey(String publicKey) throws Throwable {
        return entityRepository.findByPublicKey(publicKey).orElseThrow(new Supplier<Throwable>() {
            @Override
            public Throwable get() {
                return new NotFoundException("Entity not found");
            }
        });
    }

    public String getPublicKey(String identifier) throws Throwable {
        return get(identifier).getPublicKey();
    }

    public Entity update(String publicKey, EntityUpdateRequest entityUpdateRequest) throws Throwable {
        Entity entity = getByPublicKey(publicKey);

        if (null != entityUpdateRequest.getType() && !entityUpdateRequest.getType().isBlank()) {
            entity.setType(entityUpdateRequest.getType());
        }

        if (null != entityUpdateRequest.getIdentifier() && !entityUpdateRequest.getIdentifier().isBlank()) {
            if (entityRepository.existsByIdNotAndIdentifier(entity.getId(), entityUpdateRequest.getIdentifier())) {
                throw new ClientException("Entity " + entityUpdateRequest.getIdentifier() + " already exists");
            }

            entity.setIdentifier(entityUpdateRequest.getIdentifier());
        }

        if (null != entityUpdateRequest.getDisplayName() && !entityUpdateRequest.getDisplayName().isBlank()) {
            entity.setDisplayName(entityUpdateRequest.getDisplayName());
        }

        entity.setDescription(entityUpdateRequest.getDescription());

        entity.setModifiedOn(new Date());
        return entityRepository.save(entity);
    }

    public Entity delete(String publicKey) throws Throwable {
        Entity entity = getByPublicKey(publicKey);
        entityRepository.delete(entity);
        return entity;
    }

    private Boolean identifierExists(String identifier) {
        return entityRepository.existsByIdentifier(identifier);
    }

    private Boolean publicKeyExists(String publicKey) {
        return entityRepository.existsByPublicKey(publicKey);
    }
}
