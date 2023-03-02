package org.evernet.io.messaging.repository;

import org.evernet.io.messaging.model.Receiver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiverRepository extends MongoRepository<Receiver, String> {

    List<Receiver> findByIdentifierIn(List<String> identifiers);

    Optional<Receiver> findByIdentifier(String identifier);

    Boolean existsByIdentifier(String identifier);
}
