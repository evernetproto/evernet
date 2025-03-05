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
import org.evernet.messaging.request.ConnectionRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    private final ConnectionGroupService connectionGroupService;

    public Connection create(String connectionGroupIdentifier, ConnectionRequest request, ActorReference actorReference, NodeReference nodeReference) {
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

    public List<Connection> list(String connectionGroupIdentifier, ConnectionType type, ActorReference actorReference, NodeReference nodeReference, Pageable pageable) {
        return connectionRepository.findByConnectionGroupIdentifierAndTypeAndActorAddressAndNodeIdentifier(connectionGroupIdentifier, type, actorReference.getAddress(), nodeReference.getIdentifier(), pageable);
    }

    public Connection delete(String connectionGroupIdentifier, ConnectionType type, String address, ActorReference actorReference, NodeReference nodeReference) {
        Connection connection = connectionRepository.findByConnectionGroupIdentifierAndTypeAndAddressAndActorAddressAndNodeIdentifier(connectionGroupIdentifier, type, address, actorReference.getAddress(), nodeReference.getIdentifier())
                .orElseThrow(() -> new NotFoundException(String.format("Connection with %s not found in connection group %s", address, connectionGroupIdentifier)));
        connectionRepository.delete(connection);
        return connection;
    }

    private Boolean exists(String connectionGroupIdentifier, ConnectionType type, String address, String actorAddress, String nodeIdentifier) {
        return connectionRepository.existsByConnectionGroupIdentifierAndTypeAndAddressAndActorAddressAndNodeIdentifier(connectionGroupIdentifier, type, address, actorAddress, nodeIdentifier);
    }
}
