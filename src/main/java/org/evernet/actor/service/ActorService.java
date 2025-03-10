package org.evernet.actor.service;

import lombok.RequiredArgsConstructor;
import org.evernet.actor.model.Actor;
import org.evernet.actor.repository.ActorRepository;
import org.evernet.actor.request.ActorPasswordChangeRequest;
import org.evernet.actor.request.ActorSignUpRequest;
import org.evernet.actor.request.ActorTokenRequest;
import org.evernet.actor.request.ActorUpdateRequest;
import org.evernet.actor.response.ActorTokenResponse;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;
import org.evernet.auth.Jwt;
import org.evernet.common.exception.AuthenticationException;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotAllowedException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.common.util.Ed25519;
import org.evernet.common.util.Password;
import org.evernet.node.model.Node;
import org.evernet.node.service.NodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    private final NodeService nodeService;

    private final Jwt jwt;

    @Value("${evernet.vertex.endpoint}")
    private String vertex;

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

    public ActorTokenResponse getToken(String nodeIdentifier, ActorTokenRequest request) throws Exception {
        Node node = nodeService.get(nodeIdentifier);

        Actor actor = actorRepository.findByNodeIdentifierAndIdentifier(node.getIdentifier(), request.getIdentifier())
                .orElseThrow(AuthenticationException::new);

        if (!Password.verify(request.getPassword(), actor.getPassword())) {
            throw new NotAllowedException();
        }

        NodeReference targetNode = null;

        if (StringUtils.hasText(request.getTargetNodeAddress())) {
            targetNode = NodeReference.from(request.getTargetNodeAddress());
        }

        String token = jwt.getActorToken(
                ActorReference.builder()
                        .identifier(actor.getIdentifier())
                        .nodeIdentifier(actor.getNodeIdentifier())
                        .vertex(vertex)
                        .build(),
                Ed25519.stringToPrivateKey(node.getSigningPrivateKey()),
                targetNode
        );

        return ActorTokenResponse.builder().token(token).build();
    }

    public Actor get(String identifier, String nodeIdentifier) {
        return actorRepository.findByNodeIdentifierAndIdentifier(nodeIdentifier, identifier)
                .orElseThrow(() -> new NotFoundException(String.format("Actor %s not found on node %s", identifier, nodeIdentifier)));
    }

    public Actor update(String identifier, ActorUpdateRequest request, String nodeIdentifier) {
        Actor actor = get(identifier, nodeIdentifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            actor.setDisplayName(request.getDisplayName());
        }

        if (StringUtils.hasText(request.getType())) {
            actor.setType(request.getType());
        }

        actor.setDescription(request.getDescription());

        return actorRepository.save(actor);
    }

    public Actor changePassword(String identifier, ActorPasswordChangeRequest request, String nodeIdentifier) {
        Actor actor = get(identifier, nodeIdentifier);
        actor.setPassword(Password.hash(request.getPassword()));
        return actorRepository.save(actor);
    }

    public Actor delete(String identifier, String nodeIdentifier) {
        Actor actor = get(identifier, nodeIdentifier);
        actorRepository.delete(actor);
        return actor;
    }

    private Boolean identifierExists(String identifier, String nodeIdentifier) {
        return actorRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
