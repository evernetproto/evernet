package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.InvalidTokenException;
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

    public PublicKey getSigningPublicKey(String nodeAddress) throws GeneralSecurityException {
        String[] components = nodeAddress.split("/");

        if (components.length != 2) {
            throw new InvalidTokenException();
        }

        String currentVertexEndpoint = configService.getVertexEndpoint();

        String signingPublicKeyString = null;
        if (currentVertexEndpoint.equals(components[0])) {
            signingPublicKeyString = nodeService.get(components[1]).getSigningPublicKey();
        } else {
            signingPublicKeyString = remoteNodeService.getNode(components[1], components[0]).getSigningPublicKey();
        }

        return Ed25519KeyHelper.stringToPublicKey(signingPublicKeyString);
    }
}
