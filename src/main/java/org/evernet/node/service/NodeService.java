package org.evernet.node.service;

import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedAdmin;
import org.evernet.core.exception.ClientException;
import org.evernet.core.exception.NotFoundException;
import org.evernet.core.util.Ed25519KeyPairUtil;
import org.evernet.node.model.Node;
import org.evernet.node.repository.NodeRepository;
import org.evernet.node.request.NodeCreationRequest;
import org.evernet.node.request.NodeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

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

    public Page<Node> list(Pageable pageable) {
        return nodeRepository.findAll(pageable);
    }

    public Node get(String identifier) {
        return nodeRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new NotFoundException(String.format("Node %s not found", identifier)));
    }

    public Node update(String identifier, NodeUpdateRequest request) {
        Node node = get(identifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            node.setDisplayName(request.getDisplayName());
        }

        node.setDescription(request.getDescription());

        if (Objects.nonNull(request.getActorsSignUpeEnabled())) {
            node.setActorSignUpsEnabled(request.getActorsSignUpeEnabled());
        }

        return nodeRepository.save(node);
    }

    public Node resetSigningKeys(String identifier) throws NoSuchAlgorithmException {
        Node node = get(identifier);

        KeyPair signingKeyPair = Ed25519KeyPairUtil.generateKeyPair();
        String signingPrivateKey = Ed25519KeyPairUtil.privateKeyToString(signingKeyPair.getPrivate());
        String signingPublicKey = Ed25519KeyPairUtil.publicKeyToString(signingKeyPair.getPublic());

        node.setSigningPrivateKey(signingPrivateKey);
        node.setSigningPublicKey(signingPublicKey);

        return nodeRepository.save(node);
    }

    public Node delete(String identifier) {
        Node node = get(identifier);
        nodeRepository.delete(node);
        return node;
    }

    private Boolean exists(String identifier) {
        return nodeRepository.existsByIdentifier(identifier);
    }
}
