package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Node;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RemoteNodeService {

    private final RestTemplate restTemplate;

    private final ConfigService configService;

    public Node getNode(String nodeIdentifier, String vertexEndpoint) {
        return restTemplate.getForObject(String.format("%s://%s/api/v1/nodes/%s",
                configService.getFederationProtocol(),
                vertexEndpoint,
                nodeIdentifier
        ), Node.class);
    }
}
