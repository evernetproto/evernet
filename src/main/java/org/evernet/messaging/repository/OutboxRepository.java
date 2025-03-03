package org.evernet.messaging.repository;

import org.evernet.messaging.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, String> {

    Boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);
}
