package org.evernet.messaging.repository;

import org.evernet.messaging.model.ConnectionGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionGroupRepository extends JpaRepository<ConnectionGroup, String> {

    Boolean existsByIdentifierAndActorAddressAndNodeIdentifier(String identifier, String actorAddress, String nodeIdentifier);

    List<ConnectionGroup> findByActorAddressAndNodeIdentifier(String actorAddress, String nodeIdentifier, Pageable pageable);

    Optional<ConnectionGroup> findByIdentifierAndActorAddressAndNodeIdentifier(String identifier, String actorAddress, String nodeIdentifier);
}
