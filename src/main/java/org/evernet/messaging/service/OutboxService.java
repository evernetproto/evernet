package org.evernet.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.messaging.model.Outbox;
import org.evernet.messaging.repository.OutboxRepository;
import org.evernet.messaging.request.OutboxCreationRequest;
import org.evernet.messaging.request.OutboxUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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

    public List<Outbox> list(ActorReference actorReference, NodeReference nodeReference, Pageable pageable) {
        return outboxRepository.findByActorAddressAndNodeIdentifier(actorReference.getAddress(), nodeReference.getIdentifier(), pageable);
    }

    public Outbox get(String identifier, ActorReference actorReference, NodeReference nodeReference) {
        return outboxRepository.findByIdentifierAndActorAddressAndNodeIdentifier(identifier, actorReference.getAddress(), nodeReference.getIdentifier())
                .orElseThrow(() -> new NotFoundException(String.format("Outbox %s not found in node %s", identifier, nodeReference.getIdentifier())));
    }

    public Outbox update(String identifier, OutboxUpdateRequest request, ActorReference actorReference, NodeReference nodeReference) {
        Outbox outbox = get(identifier, actorReference, nodeReference);

        if (StringUtils.hasText(request.getDisplayName())) {
            outbox.setDisplayName(request.getDisplayName());
        }

        outbox.setDescription(request.getDescription());
        return outboxRepository.save(outbox);
    }

    public Outbox delete(String identifier, ActorReference actorReference, NodeReference nodeReference) {
        Outbox outbox = get(identifier, actorReference, nodeReference);
        outboxRepository.delete(outbox);
        return outbox;
    }

    private Boolean identifierExists(String identifier, String nodeIdentifier) {
        return outboxRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
