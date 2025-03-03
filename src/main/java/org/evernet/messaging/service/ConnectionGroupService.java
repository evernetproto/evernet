package org.evernet.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.evernet.common.exception.ClientException;
import org.evernet.messaging.model.ConnectionGroup;
import org.evernet.messaging.repository.ConnectionGroupRepository;
import org.evernet.messaging.request.ConnectionGroupCreationRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectionGroupService {

    private final ConnectionGroupRepository connectionGroupRepository;

    public ConnectionGroup create(ConnectionGroupCreationRequest request, ActorReference actorReference, NodeReference nodeReference) {
        if (identifierExists(request.getIdentifier(), actorReference.getAddress(), nodeReference.getIdentifier())) {
            throw new ClientException(String.format("Connection group %s already exists in node %s", request.getIdentifier(), actorReference.getAddress()));
        }

        ConnectionGroup connectionGroup = ConnectionGroup.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .actorAddress(actorReference.getAddress())
                .nodeIdentifier(nodeReference.getIdentifier())
                .build();

        return connectionGroupRepository.save(connectionGroup);
    }

    private Boolean identifierExists(String identifier, String actorAddress, String nodeIdentifier) {
        return connectionGroupRepository.existsByIdentifierAndActorAddressAndNodeIdentifier(identifier, actorAddress, nodeIdentifier);
    }
}
