package org.evernet.messaging.repository;

import org.evernet.messaging.model.ConnectionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionGroupRepository extends JpaRepository<ConnectionGroup, String> {

    Boolean existsByIdentifierAndActorAddressAndNodeIdentifier(String identifier, String actorAddress, String nodeIdentifier);
}
