package org.evernet.identity.repository;

import org.evernet.identity.model.KnownEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KnownEntityRepository extends MongoRepository<KnownEntity, String> {

    Optional<KnownEntity> findByPublicKey(String publicKey);
}
