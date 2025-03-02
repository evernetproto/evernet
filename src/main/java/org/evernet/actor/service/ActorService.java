package org.evernet.actor.service;

import lombok.RequiredArgsConstructor;
import org.evernet.actor.model.Actor;
import org.evernet.actor.repository.ActorRepository;
import org.evernet.actor.request.ActorSignUpRequest;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.common.util.Password;
import org.evernet.node.service.NodeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    private final NodeService nodeService;

    public Actor signUp(String nodeIdentifier, ActorSignUpRequest request) {
        if (identifierExists(request.getIdentifier(), nodeIdentifier)) {
            throw new ClientException(String.format("Actor %s already exists on node %s", request.getIdentifier(), nodeIdentifier));
        }

        if (!nodeService.identifierExists(nodeIdentifier)) {
            throw new NotFoundException(String.format("Node %s not found", nodeIdentifier));
        }

        Actor actor = Actor.builder()
                .nodeIdentifier(nodeIdentifier)
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .displayName(request.getDisplayName())
                .type(request.getType())
                .description(request.getDescription())
                .build();

        return actorRepository.save(actor);
    }

    private Boolean identifierExists(String identifier, String nodeIdentifier) {
        return actorRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
