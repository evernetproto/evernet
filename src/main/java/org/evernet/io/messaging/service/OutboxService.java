package org.evernet.io.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedActor;
import org.evernet.core.exception.ClientException;
import org.evernet.core.exception.NotFoundException;
import org.evernet.io.messaging.model.Outbox;
import org.evernet.io.messaging.repository.OutboxRepository;
import org.evernet.io.messaging.request.OutboxCreationRequest;
import org.evernet.io.messaging.request.OutboxUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public Page<Outbox> list(String actorAddress, Pageable pageable) {
        return outboxRepository.findByActorAddress(actorAddress, pageable);
    }

    public Outbox get(String identifier, AuthenticatedActor actor) {
        return outboxRepository.findByIdentifierAndActorAddressAndNodeIdentifier(identifier, actor.getAddress(), actor.getTargetNodeIdentifier())
                .orElseThrow(() -> new NotFoundException(String.format("Outbox %s not found on node %s", identifier, actor.getTargetNodeIdentifier())));
    }

    public Outbox update(String identifier, OutboxUpdateRequest request, AuthenticatedActor actor) {
        Outbox outbox = get(identifier, actor);

        if (StringUtils.hasText(request.getDisplayName())) {
            outbox.setDisplayName(request.getDisplayName());
        }

        outbox.setDescription(request.getDescription());
        return outboxRepository.save(outbox);
    }

    public Outbox delete(String identifier, AuthenticatedActor actor) {
        Outbox outbox = get(identifier, actor);
        outboxRepository.delete(outbox);
        return outbox;
    }

    private Boolean identifierExists(String identifier, String nodeIdentifier) {
        return outboxRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
