package org.evernet.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.messaging.model.Inbox;
import org.evernet.messaging.repository.InboxRepository;
import org.evernet.messaging.request.InboxCreationRequest;
import org.evernet.messaging.request.InboxUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InboxService {

    private final InboxRepository inboxRepository;

    public Inbox create(InboxCreationRequest request, NodeReference node, ActorReference actor) {
        if (identifierExists(request.getIdentifier(), node.getIdentifier())) {
            throw new ClientException(String.format("Inbox %s already exists in node %s", request.getIdentifier(), node.getIdentifier()));
        }

        Inbox inbox = Inbox.builder()
                .identifier(request.getIdentifier())
                .nodeIdentifier(node.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .actorAddress(actor.getAddress())
                .build();

        return inboxRepository.save(inbox);
    }

    public List<Inbox> list(ActorReference actorReference, NodeReference nodeReference, Pageable pageable ) {
        return inboxRepository.findByActorAddressAndNodeIdentifier(actorReference.getAddress(), nodeReference.getIdentifier(), pageable);
    }

    public Inbox get(String identifier, ActorReference actorReference, NodeReference nodeReference) {
        return inboxRepository.findByIdentifierAndActorAddressAndNodeIdentifier(identifier, actorReference.getAddress(), nodeReference.getIdentifier())
                .orElseThrow(() -> new NotFoundException(String.format("Inbox %s not found in node %s", identifier, nodeReference.getIdentifier())));
    }

    public Inbox update(String identifier, InboxUpdateRequest request, ActorReference actorReference, NodeReference nodeReference) {
        Inbox inbox = get(identifier, actorReference, nodeReference);

        if (StringUtils.hasText(request.getDisplayName())) {
            inbox.setDisplayName(request.getDisplayName());
        }

        inbox.setDescription(request.getDescription());

        return inboxRepository.save(inbox);
    }

    private Boolean identifierExists(String identifier, String nodeIdentifier) {
        return inboxRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
