package org.evernet.actor.service;

import lombok.RequiredArgsConstructor;
import org.evernet.actor.model.Actor;
import org.evernet.actor.repository.ActorRepository;
import org.evernet.actor.request.ActorSignUpRequest;
import org.evernet.actor.request.ActorTokenRequest;
import org.evernet.actor.response.ActorTokenResponse;
import org.evernet.core.auth.AuthenticatedActor;
import org.evernet.core.auth.Jwt;
import org.evernet.core.exception.AuthenticationException;
import org.evernet.core.exception.ClientException;
import org.evernet.core.exception.NotAllowedException;
import org.evernet.core.exception.NotFoundException;
import org.evernet.core.util.Ed25519KeyPairUtil;
import org.evernet.core.util.Password;
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

    @Value("${evernet.vertex}")
    private String vertex;

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

    public ActorTokenResponse getToken(ActorTokenRequest request) throws Exception {
        Node node = nodeService.get(request.getNodeIdentifier());

        if (!StringUtils.hasText(request.getTargetNodeAddress())) {
            request.setTargetNodeAddress(String.format("%s/%s", vertex, request.getNodeIdentifier()));
        }

        String[] targetNodeComponents = request.getTargetNodeAddress().split("/");

        if (targetNodeComponents.length != 2) {
            throw new ClientException("Invalid target node address");
        }

        Actor actor = actorRepository.findByIdentifierAndNodeIdentifier(request.getIdentifier(), request.getNodeIdentifier())
                .orElseThrow(AuthenticationException::new);

        if (!Password.verify(request.getPassword(), actor.getPassword())) {
            throw new AuthenticationException();
        }

        String token = jwt.getActorToken(AuthenticatedActor.builder()
                .identifier(actor.getIdentifier())
                .sourceNodeIdentifier(request.getNodeIdentifier())
                .sourceVertex(vertex)
                .targetVertex(targetNodeComponents[0])
                .targetNodeIdentifier(targetNodeComponents[1])
                .build(), Ed25519KeyPairUtil.stringToPrivateKey(node.getSigningPrivateKey()));

        return ActorTokenResponse.builder()
                .token(token)
                .build();
    }

    public Actor get(String identifier, String nodeIdentifier) {
        return actorRepository.findByIdentifierAndNodeIdentifier(identifier, nodeIdentifier)
                .orElseThrow(() -> new NotFoundException(String.format("Actor %s not found on node %s", identifier, nodeIdentifier)));
    }

    private Boolean exists(String identifier, String nodeIdentifier) {
        return actorRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
