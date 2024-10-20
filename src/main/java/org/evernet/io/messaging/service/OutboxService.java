package org.evernet.io.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedActor;
import org.evernet.core.exception.ClientException;
import org.evernet.io.messaging.model.Outbox;
import org.evernet.io.messaging.repository.OutboxRepository;
import org.evernet.io.messaging.request.OutboxCreationRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    public Outbox create(OutboxCreationRequest request, AuthenticatedActor actor) {
        if (identifierExists(request.getIdentifier(), actor.getTargetNodeIdentifier())) {
            throw new ClientException(String.format("Outbox %s already exists on node %s", request.getIdentifier(), actor.getTargetNodeIdentifier()));
        }

        Outbox outbox = Outbox.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .actorAddress(actor.getAddress())
                .nodeIdentifier(actor.getTargetNodeAddress())
                .build();

        return outboxRepository.save(outbox);
    }

    private Boolean identifierExists(String identifier, String nodeIdentifier) {
        return outboxRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
