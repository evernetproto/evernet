package org.evernet.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.evernet.model.Config;
import org.evernet.repository.ConfigRepository;
import org.evernet.util.Random;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    private static final String KEY_JWT_SIGNING_KEY = "JWT_SIGNING_KEY";
    private static final String KEY_VERTEX_ENDPOINT = "VERTEX_ENDPOINT";
    private static final String KEY_VERTEX_DISPLAY_NAME = "VERTEX_DISPLAY_NAME";
    private static final String KEY_VERTEX_DESCRIPTION = "VERTEX_DESCRIPTION";

    @PostConstruct
    public void init() {
        if (!configRepository.existsByKey(KEY_JWT_SIGNING_KEY)) {
            Config config = Config.builder()
                    .key(KEY_JWT_SIGNING_KEY)
                    .value(Random.generateRandomString(128))
                    .build();
            configRepository.save(config);
        }
    }

    public void initVertex(String endpoint, String displayName, String description) {
        List<Config> configs = List.of(
                Config.builder().key(KEY_VERTEX_ENDPOINT).value(endpoint).build(),
                Config.builder().key(KEY_VERTEX_DISPLAY_NAME).value(displayName).build(),
                Config.builder().key(KEY_VERTEX_DESCRIPTION).value(description).build()
        );

        configRepository.saveAll(configs);
    }

    public Config get(String key, String defaultValue) {
        Config config = configRepository.findByKey(key);

        if (config == null) {
            return Config.builder().key(key).value(defaultValue).build();
        }

        return config;
    }

    public String getJwtSigningKey() {
        return get(KEY_JWT_SIGNING_KEY, "secret").getValue();
    }

    public String getVertexEndpoint() {
        return get(KEY_VERTEX_ENDPOINT, null).getValue();
    }
}
