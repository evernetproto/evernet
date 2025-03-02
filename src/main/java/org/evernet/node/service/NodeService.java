package org.evernet.node.service;

import lombok.RequiredArgsConstructor;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.common.util.Ed25519;
import org.evernet.node.model.Node;
import org.evernet.node.repository.NodeRepository;
import org.evernet.node.request.NodeCreationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    public Node create(NodeCreationRequest request, String creator) throws Exception {
        if (identifierExists(request.getIdentifier())) {
            throw new ClientException(String.format("Node %s already exists", request.getIdentifier()));
        }

        KeyPair signingKeyPair = Ed25519.generateKeyPair();
        String signingPrivateKeyString = Ed25519.privateKeyToString(signingKeyPair.getPrivate());
        String signingPublicKeyString = Ed25519.publicKeyToString(signingKeyPair.getPublic());

        Node node = Node.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .signingPrivateKey(signingPrivateKeyString)
                .signingPublicKey(signingPublicKeyString)
                .creator(creator)
                .build();

        return nodeRepository.save(node);
    }

    public List<Node> list(Pageable pageable) {
        return nodeRepository.findAll(pageable).getContent();
    }

    public Node get(String identifier) {
        return nodeRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new NotFoundException(String.format("Node %s not found", identifier)));
    }

    private Boolean identifierExists(String identifier) {
        return nodeRepository.existsByIdentifier(identifier);
    }
}
