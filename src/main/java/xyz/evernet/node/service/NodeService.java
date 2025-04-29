package xyz.evernet.node.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.evernet.core.exception.ClientException;
import xyz.evernet.core.exception.NotFoundException;
import xyz.evernet.core.key.Ed25519Util;
import xyz.evernet.node.model.Node;
import xyz.evernet.node.repository.NodeRepository;
import xyz.evernet.node.request.NodeCreationRequest;
import xyz.evernet.node.request.NodeUpdateRequest;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    public Node create(NodeCreationRequest request, String creator) throws NoSuchAlgorithmException {
        if (nodeRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException(String.format("Node %s already exists", request.getIdentifier()));
        }

        KeyPair keyPair = Ed25519Util.generateKeyPair();
        String signingPrivateKey = Ed25519Util.privateKeyToBase64(keyPair.getPrivate());
        String signingPublicKey = Ed25519Util.publicKeyToBase64(keyPair.getPublic());

        Node node = Node.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .open(request.getOpen())
                .signingPrivateKey(signingPrivateKey)
                .signingPublicKey(signingPublicKey)
                .open(request.getOpen())
                .build();

        return nodeRepository.save(node);
    }

    public List<Node> listAll(Pageable pageable) {
        return nodeRepository.findAll(pageable).getContent();
    }

    public List<Node> listOpen(Pageable pageable) {
        return nodeRepository.findByOpen(true, pageable);
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

        return nodeRepository.save(node);
    }

    public Node resetSigningKey(String identifier) throws NoSuchAlgorithmException {
        Node node = get(identifier);
        KeyPair keyPair = Ed25519Util.generateKeyPair();
        String signingPrivateKey = Ed25519Util.privateKeyToBase64(keyPair.getPrivate());
        String signingPublicKey = Ed25519Util.publicKeyToBase64(keyPair.getPublic());
        node.setSigningPrivateKey(signingPrivateKey);
        node.setSigningPublicKey(signingPublicKey);
        return nodeRepository.save(node);
    }

    public Node delete(String identifier) {
        Node node = get(identifier);
        nodeRepository.delete(node);
        return node;
    }
}
