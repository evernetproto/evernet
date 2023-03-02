package org.evernet.identity.repository;

import org.evernet.identity.model.Entity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntityRepository extends MongoRepository<Entity, String> {

    Optional<Entity> findByIdentifier(String identifier);

    Optional<Entity> findByPublicKey(String publicKey);

    Boolean existsByIdentifier(String identifier);

    Boolean existsByIdNotAndIdentifier(String id, String identifier);

    Boolean existsByPublicKey(String publicKey);
}
