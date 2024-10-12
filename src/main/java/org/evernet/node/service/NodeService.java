package org.evernet.node.service;

import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedAdmin;
import org.evernet.core.exception.ClientException;
import org.evernet.core.util.Ed25519KeyPairUtil;
import org.evernet.node.model.Node;
import org.evernet.node.repository.NodeRepository;
import org.evernet.node.request.NodeCreationRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    public Node create(NodeCreationRequest request, AuthenticatedAdmin creator) throws NoSuchAlgorithmException {
        if (exists(request.getIdentifier())) {
            throw new ClientException(String.format("Node %s already exists", request.getIdentifier()));
        }

        KeyPair signingKeyPair = Ed25519KeyPairUtil.generateKeyPair();
        String signingPrivateKey = Ed25519KeyPairUtil.privateKeyToString(signingKeyPair.getPrivate());
        String signingPublicKey = Ed25519KeyPairUtil.publicKeyToString(signingKeyPair.getPublic());

        Node node = Node.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .actorSignUpsEnabled(request.getActorsSignUpEnabled())
                .creator(creator.getIdentifier())
                .signingPrivateKey(signingPrivateKey)
                .signingPublicKey(signingPublicKey)
                .build();

        return nodeRepository.save(node);
    }

    public Page<Node> list() {
        return null;
    }

    public Node get() {
        return null;
    }

    public Node update() {
        return null;
    }

    public Node delete() {
        return null;
    }

    private Boolean exists(String identifier) {
        return nodeRepository.existsByIdentifier(identifier);
    }
}
