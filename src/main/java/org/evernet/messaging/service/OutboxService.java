package org.evernet.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.evernet.common.exception.ClientException;
import org.evernet.messaging.model.Outbox;
import org.evernet.messaging.repository.OutboxRepository;
import org.evernet.messaging.request.OutboxCreationRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    public Outbox create(OutboxCreationRequest request, ActorReference actorReference, NodeReference nodeReference) {
        if (identifierExists(request.getIdentifier(), nodeReference.getIdentifier())) {
            throw new ClientException(String.format("Outbox %s already exists in node %s", request.getIdentifier(), nodeReference.getIdentifier()));
        }

        Outbox outbox = Outbox.builder()
                .identifier(request.getIdentifier())
                .nodeIdentifier(nodeReference.getIdentifier())
                .actorAddress(actorReference.getAddress())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .build();

        return outboxRepository.save(outbox);
    }

    private Boolean identifierExists(String identifier, String nodeIdentifier) {
        return outboxRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
