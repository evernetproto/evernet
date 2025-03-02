package org.evernet.health.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.health.response.VertexInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VertexInfoController {

    @Value("${evernet.vertex.identifier}")
    private String identifier;

    @Value("${evernet.vertex.display-name}")
    private String displayName;

    @Value("${evernet.vertex.description}")
    private String description;

    @Value("${evernet.vertex.endpoint}")
    private String endpoint;

    @GetMapping("/vertex/info")
    public VertexInfoResponse getVertexInfo() {
        return VertexInfoResponse.builder()
                .identifier(identifier)
                .displayName(displayName)
                .description(description)
                .endpoint(endpoint)
                .build();
    }
}
