package org.evernet.repository;

import org.evernet.model.MessageGateway;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageGatewayRepository extends JpaRepository<MessageGateway, String> {
    boolean existsByNodeIdentifierAndIdentifier(String nodeIdentifier, String identifier);

    List<MessageGateway> findByNodeIdentifierAndActorAddress(String nodeIdentifier, String actorAddress, Pageable pageable);

    MessageGateway findByIdentifierAndNodeIdentifierAndActorAddress(String identifier, String nodeIdentifier, String actorAddress);

    MessageGateway findByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);
}
