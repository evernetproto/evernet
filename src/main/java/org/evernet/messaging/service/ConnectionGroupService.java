package org.evernet.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.messaging.model.ConnectionGroup;
import org.evernet.messaging.repository.ConnectionGroupRepository;
import org.evernet.messaging.request.ConnectionGroupCreationRequest;
import org.evernet.messaging.request.ConnectionGroupUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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

    public List<ConnectionGroup> list(ActorReference actorReference, NodeReference nodeReference, Pageable pageable) {
        return connectionGroupRepository.findByActorAddressAndNodeIdentifier(actorReference.getAddress(), nodeReference.getIdentifier(), pageable);
    }

    public ConnectionGroup get(String identifier, ActorReference actorReference, NodeReference nodeReference) {
        return connectionGroupRepository.findByIdentifierAndActorAddressAndNodeIdentifier(identifier,
                actorReference.getAddress(), nodeReference.getIdentifier())
                .orElseThrow(() -> new NotFoundException(String.format("Connection group %s not found in node %s", identifier, nodeReference.getIdentifier())));
    }

    public ConnectionGroup update(String identifier, ConnectionGroupUpdateRequest request, ActorReference actorReference, NodeReference nodeReference) {
        ConnectionGroup connectionGroup = get(identifier, actorReference, nodeReference);

        if (StringUtils.hasText(request.getDisplayName())) {
            connectionGroup.setDisplayName(request.getDisplayName());
        }

        connectionGroup.setDescription(request.getDescription());

        return connectionGroupRepository.save(connectionGroup);
    }

    public ConnectionGroup delete(String identifier, ActorReference actorReference, NodeReference nodeReference) {
        ConnectionGroup connectionGroup = get(identifier, actorReference, nodeReference);
        connectionGroupRepository.delete(connectionGroup);
        return connectionGroup;
    }

    public Boolean identifierExists(String identifier, String actorAddress, String nodeIdentifier) {
        return connectionGroupRepository.existsByIdentifierAndActorAddressAndNodeIdentifier(identifier, actorAddress, nodeIdentifier);
    }
}
