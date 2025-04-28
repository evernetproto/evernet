package xyz.evernet.vertex.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.util.Random;
import xyz.evernet.vertex.model.VertexConfig;
import xyz.evernet.vertex.repository.VertexConfigRepository;

@Component
@RequiredArgsConstructor
public class VertexConfigService {

    private final VertexConfigRepository vertexConfigRepository;

    private static final String KEY_JWT_SIGNING_KEY = "JWT_SIGNING_KEY";

    @PostConstruct
    public void init() {
        if (!vertexConfigRepository.existsByKey(KEY_JWT_SIGNING_KEY)) {
            vertexConfigRepository.save(VertexConfig.builder()
                            .key(KEY_JWT_SIGNING_KEY)
                            .value(Random.generateRandomString(256))
                    .build());
        }
    }
}
