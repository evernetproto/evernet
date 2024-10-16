package org.evernet.node.service;

import lombok.RequiredArgsConstructor;
import org.evernet.node.model.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RemoteNodeService {

    private final RestTemplate restTemplate;

    @Value("${evernet.vertex.peer.protocol}")
    private String protocol;

    public Node get(String vertex, String identifier) {
        String url = String.format("%s://%s/api/v1/nodes/%s", protocol, vertex, identifier);
        return restTemplate.getForObject(url, Node.class);
    }
}
