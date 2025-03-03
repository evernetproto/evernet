package org.evernet.messaging.repository;

import org.evernet.messaging.model.Inbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, String> {

    Boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    List<Inbox> findByActorAddressAndNodeIdentifier(String actorAddress, String nodeIdentifier, Pageable pageable);

    Optional<Inbox> findByIdentifierAndActorAddressAndNodeIdentifier(String identifier, String actorAddress, String nodeIdentifier);
}
