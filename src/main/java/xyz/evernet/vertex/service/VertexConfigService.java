package xyz.evernet.vertex.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.core.exception.ServerException;
import xyz.evernet.core.util.Random;
import xyz.evernet.vertex.model.VertexConfig;
import xyz.evernet.vertex.repository.VertexConfigRepository;

@Component
@RequiredArgsConstructor
public class VertexConfigService {

    private final VertexConfigRepository vertexConfigRepository;

    private static final String KEY_JWT_SIGNING_KEY = "JWT_SIGNING_KEY";

    private static final String KEY_VERTEX_ENDPOINT = "VERTEX_ENDPOINT";

    @PostConstruct
    public void init() {
        if (!vertexConfigRepository.existsByKey(KEY_JWT_SIGNING_KEY)) {
            vertexConfigRepository.save(VertexConfig.builder()
                            .key(KEY_JWT_SIGNING_KEY)
                            .value(Random.generateRandomString(256))
                    .build());
        }
    }

    public String getJwtSigningKey() {
        VertexConfig config = vertexConfigRepository.findByKey(KEY_JWT_SIGNING_KEY)
                .orElseThrow(() -> new ServerException("Vertex is not initialized"));

        return config.getValue();
    }

    public void setVertexEndpoint(String vertexEndpoint) {
        VertexConfig vertexConfig = vertexConfigRepository.findByKey(KEY_VERTEX_ENDPOINT)
                .orElse(VertexConfig.builder().key(KEY_VERTEX_ENDPOINT).value(vertexEndpoint).build());
        vertexConfig.setValue(vertexEndpoint);
        vertexConfigRepository.save(vertexConfig);
    }

    public String getVertexEndpoint() {
        VertexConfig vertexConfig = vertexConfigRepository.findByKey(KEY_VERTEX_ENDPOINT)
                .orElseThrow(() -> new ServerException("Vertex is not initialized"));

        return vertexConfig.getValue();
    }
}
