package org.evernet.io.messaging.repository;

import org.evernet.io.messaging.model.Outbox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, String> {

    Boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    Page<Outbox> findByActorAddress(String actorAddress, Pageable pageable);

    Optional<Outbox> findByIdentifierAndActorAddressAndNodeIdentifier(String identifier, String actorAddress, String nodeIdentifier);
}
