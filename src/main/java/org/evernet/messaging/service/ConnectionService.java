package org.evernet.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.InboxReference;
import org.evernet.common.address.NodeReference;
import org.evernet.common.address.OutboxReference;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.messaging.enums.ConnectionType;
import org.evernet.messaging.model.Connection;
import org.evernet.messaging.repository.ConnectionRepository;
import org.evernet.messaging.request.ConnectionCreationRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    private final ConnectionGroupService connectionGroupService;

    public Connection create(String connectionGroupIdentifier, ConnectionCreationRequest request, ActorReference actorReference, NodeReference nodeReference) {
        if (exists(connectionGroupIdentifier, request.getType(), request.getAddress(), actorReference.getAddress(), nodeReference.getIdentifier())) {
            throw new ClientException(String.format("Connection with %s already exists in group %s", request.getAddress(), connectionGroupIdentifier));
        }

        if (!connectionGroupService.identifierExists(connectionGroupIdentifier, actorReference.getAddress(), nodeReference.getAddress())) {
            throw new NotFoundException(String.format("Connection group %s not found", connectionGroupIdentifier));
        }

        String address;
        switch (request.getType()) {
            case INBOX -> {
                InboxReference inboxReference = InboxReference.from(request.getAddress());
                address = inboxReference.getAddress();
            }
            case OUTBOX -> {
                OutboxReference outboxReference = OutboxReference.from(request.getAddress());
                address = outboxReference.getAddress();
            }
            default -> throw new ClientException(String.format("Connection type %s not supported", request.getType()));
        }

        Connection connection = Connection.builder()
                .connectionGroupIdentifier(connectionGroupIdentifier)
                .type(request.getType())
                .address(address)
                .actorAddress(actorReference.getAddress())
                .nodeIdentifier(nodeReference.getIdentifier())
                .build();

        return connectionRepository.save(connection);
    }

    private Boolean exists(String connectionGroupIdentifier, ConnectionType type, String address, String actorAddress, String nodeIdentifier) {
        return connectionRepository.existsByConnectionGroupIdentifierAndTypeAndAddressAndActorAddressAndNodeIdentifier(connectionGroupIdentifier, type, address, actorAddress, nodeIdentifier);
    }
}
