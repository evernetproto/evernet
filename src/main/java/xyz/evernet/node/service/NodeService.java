package xyz.evernet.node.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.evernet.core.exception.ClientException;
import xyz.evernet.core.key.Ed25519Util;
import xyz.evernet.node.model.Node;
import xyz.evernet.node.repository.NodeRepository;
import xyz.evernet.node.request.NodeCreationRequest;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

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
}
