package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.NodeAddress;
import org.evernet.util.Ed25519KeyHelper;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

@Service
@RequiredArgsConstructor
public class NodeKeyService {

    private final NodeService nodeService;

    private final RemoteNodeService remoteNodeService;

    private final ConfigService configService;

    public PublicKey getSigningPublicKey(String nodeAddressString) throws GeneralSecurityException {
        NodeAddress nodeAddress = NodeAddress.fromString(nodeAddressString);

        String currentVertexEndpoint = configService.getVertexEndpoint();

        String signingPublicKeyString;
        if (currentVertexEndpoint.equals(nodeAddress.getVertexEndpoint())) {
            signingPublicKeyString = nodeService.get(nodeAddress.getNodeIdentifier()).getSigningPublicKey();
        } else {
            signingPublicKeyString = remoteNodeService.getNode(nodeAddress.getNodeIdentifier(), nodeAddress.getVertexEndpoint()).getSigningPublicKey();
        }

        return Ed25519KeyHelper.stringToPublicKey(signingPublicKeyString);
    }
}
