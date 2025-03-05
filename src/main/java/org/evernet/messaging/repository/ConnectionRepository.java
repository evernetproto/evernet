package org.evernet.messaging.repository;

import org.evernet.messaging.enums.ConnectionType;
import org.evernet.messaging.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, String> {

    Boolean existsByConnectionGroupIdentifierAndTypeAndAddressAndActorAddressAndNodeIdentifier(String connectionGroupIdentifier, ConnectionType type, String address, String actorAddress, String nodeIdentifier);
}
