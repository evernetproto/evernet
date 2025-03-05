package org.evernet.messaging.repository;

import org.evernet.messaging.enums.ConnectionType;
import org.evernet.messaging.model.Connection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, String> {

    Boolean existsByConnectionGroupIdentifierAndTypeAndAddressAndActorAddressAndNodeIdentifier(String connectionGroupIdentifier, ConnectionType type, String address, String actorAddress, String nodeIdentifier);

    List<Connection> findByConnectionGroupIdentifierAndTypeAndActorAddressAndNodeIdentifier(String connectionGroupIdentifier, ConnectionType type, String actorAddress, String nodeIdentifier, Pageable pageable);

    Optional<Connection> findByConnectionGroupIdentifierAndTypeAndAddressAndActorAddressAndNodeIdentifier(String connectionGroupIdentifier, ConnectionType type, String address, String actorAddress, String nodeIdentifier);
}
