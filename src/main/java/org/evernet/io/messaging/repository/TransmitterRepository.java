package org.evernet.io.messaging.repository;

import org.evernet.io.messaging.model.Transmitter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransmitterRepository extends MongoRepository<Transmitter, String> {

    List<Transmitter> findByIdentifierIn(List<String> identifiers);

    Optional<Transmitter> findByIdentifier(String identifier);

    Boolean existsByIdentifier(String identifier);
}
