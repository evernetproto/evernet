package org.evernet.auth;

import lombok.RequiredArgsConstructor;
import org.evernet.node.model.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RemoteNodeService {

    @Value("${evernet.vertex.federation.protocol}")
    private String federationProtocol;

    private final RestTemplate restTemplate;

    public Node get(String vertex, String nodeIdentifier) {
        return restTemplate.getForObject(String.format("%s://%s/api/v1/nodes/%s", federationProtocol, vertex, nodeIdentifier), Node.class);
    }
}
