package org.evernet.actor.service;

import lombok.RequiredArgsConstructor;
import org.evernet.actor.model.Actor;
import org.evernet.actor.repository.ActorRepository;
import org.evernet.actor.request.ActorSignUpRequest;
import org.evernet.core.exception.ClientException;
import org.evernet.core.exception.NotAllowedException;
import org.evernet.core.util.Password;
import org.evernet.node.model.Node;
import org.evernet.node.service.NodeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    private final NodeService nodeService;

    public Actor signUp(ActorSignUpRequest request) {
        Node node = nodeService.get(request.getNodeIdentifier());

        if (!node.getActorSignUpsEnabled()) {
            throw new NotAllowedException();
        }

        if (exists(request.getIdentifier(), request.getNodeIdentifier())) {
            throw new ClientException(String.format("Actor with identifier %s already exists", request.getIdentifier()));
        }

        Actor actor = Actor.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .type(request.getType())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .nodeIdentifier(node.getIdentifier())
                .build();

        return actorRepository.save(actor);
    }

    private Boolean exists(String identifier, String nodeIdentifier) {
        return actorRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }

}
