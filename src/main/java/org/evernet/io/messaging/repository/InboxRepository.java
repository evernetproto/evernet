package org.evernet.io.messaging.repository;

import org.evernet.io.messaging.model.Inbox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, String> {

    Boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    Page<Inbox> findByActorAddressAndNodeIdentifier(String actorAddress, String nodeIdentifier, Pageable pageable);

    Optional<Inbox> findByIdentifierAndActorAddressAndNodeIdentifier(String identifier, String actorAddress, String nodeIdentifier);
}
