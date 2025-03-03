package org.evernet.messaging.repository;

import org.evernet.messaging.model.Outbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, String> {

    Boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    List<Outbox> findByActorAddressAndNodeIdentifier(String actorAddress, String nodeIdentifier, Pageable pageable);

    Optional<Outbox> findByIdentifierAndActorAddressAndNodeIdentifier(String identifier, String actorAddress, String nodeIdentifier);
}
