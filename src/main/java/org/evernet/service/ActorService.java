package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.Jwt;
import org.evernet.exception.AuthenticationException;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotAllowedException;
import org.evernet.model.Actor;
import org.evernet.model.Node;
import org.evernet.repository.ActorRepository;
import org.evernet.request.ActorSignUpRequest;
import org.evernet.request.ActorTokenRequest;
import org.evernet.response.ActorTokenResponse;
import org.evernet.util.Ed25519KeyHelper;
import org.evernet.util.Password;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    private final NodeService nodeService;
    private final Jwt jwt;

    public Actor signUp(String nodeIdentifier, ActorSignUpRequest request) {
        Node node = nodeService.get(nodeIdentifier);

        if (!node.getOpen()) {
            throw new NotAllowedException();
        }

        if (actorRepository.existsByIdentifierAndNodeIdentifier(request.getIdentifier(), nodeIdentifier)) {
            throw new ClientException(String.format("Actor with identifier %s already exists on node %s", request.getIdentifier(), node.getIdentifier()));
        }

        Actor actor = Actor.builder()
                .nodeIdentifier(node.getIdentifier())
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .type(request.getType())
                .password(Password.hash(request.getPassword()))
                .build();

        return actorRepository.save(actor);
    }

    public ActorTokenResponse getToken(String nodeIdentifier, ActorTokenRequest request) throws GeneralSecurityException {
        Node node = nodeService.get(nodeIdentifier);
        Actor actor = actorRepository.findByNodeIdentifierAndIdentifier(nodeIdentifier, request.getIdentifier());

        if (actor == null) {
            throw new AuthenticationException();
        }

        if (!Password.verify(request.getPassword(), actor.getPassword())) {
            throw new NotAllowedException();
        }

        String token = jwt.getActorToken(actor.getIdentifier(), actor.getNodeIdentifier(), request.getTargetNodeAddress(), Ed25519KeyHelper.stringToPrivateKey(node.getSigningPrivateKey()));
        return ActorTokenResponse.builder().token(token).build();
    }
}
