package xyz.evernet.vertex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.vertex.response.VertexInfoResponse;
import xyz.evernet.vertex.service.VertexConfigService;

@RestController
@RequiredArgsConstructor
public class VertexInfoController {

    private final VertexConfigService vertexConfigService;

    @GetMapping("/info")
    public VertexInfoResponse getVertexInfo() {
        return VertexInfoResponse.builder()
                .endpoint(vertexConfigService.getVertexEndpoint())
                .build();
    }
}
