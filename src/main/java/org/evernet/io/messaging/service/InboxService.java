package org.evernet.io.messaging.service;

import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedActor;
import org.evernet.core.exception.ClientException;
import org.evernet.core.exception.NotFoundException;
import org.evernet.io.messaging.model.Inbox;
import org.evernet.io.messaging.repository.InboxRepository;
import org.evernet.io.messaging.request.InboxCreationRequest;
import org.evernet.io.messaging.request.InboxUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class InboxService {

    private final InboxRepository inboxRepository;

    public Inbox create(InboxCreationRequest request, AuthenticatedActor actor) {
        if (identifierExists(request.getIdentifier(), actor.getTargetNodeIdentifier())) {
            throw new ClientException(String.format("Inbox %s already exists on node %s", request.getIdentifier(), actor.getTargetNodeIdentifier()));
        }

        Inbox inbox = Inbox.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .actorAddress(actor.getAddress())
                .nodeIdentifier(actor.getTargetNodeIdentifier())
                .build();

        return inboxRepository.save(inbox);
    }

    public Page<Inbox> list(AuthenticatedActor actor, Pageable pageable) {
        return inboxRepository.findByActorAddressAndNodeIdentifier(actor.getAddress(), actor.getTargetNodeIdentifier(), pageable);
    }

    public Inbox get(String identifier, AuthenticatedActor actor) {
        return inboxRepository.findByIdentifierAndActorAddressAndNodeIdentifier(identifier, actor.getAddress(), actor.getTargetNodeIdentifier())
                .orElseThrow(() -> new NotFoundException(String.format("Inbox %s not found on node %s", identifier, actor.getTargetNodeIdentifier())));
    }

    public Inbox update(String identifier, InboxUpdateRequest request, AuthenticatedActor actor) {
        Inbox inbox = get(identifier, actor);

        if (StringUtils.hasText(request.getDisplayName())) {
            inbox.setDisplayName(request.getDisplayName());
        }

        inbox.setDescription(request.getDescription());
        return inboxRepository.save(inbox);
    }

    public Inbox delete(String identifier, AuthenticatedActor actor) {
        Inbox inbox = get(identifier, actor);
        inboxRepository.delete(inbox);
        return inbox;
    }

    private Boolean identifierExists(String identifier, String nodeIdentifier) {
        return inboxRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
