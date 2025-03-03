package org.evernet.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Value("${evernet.vertex.resttemplate.default.read-timeout}")
    private Integer readTimeout;

    @Value("${evernet.vertex.resttemplate.default.connect-timeout}")
    private Integer connectTimeout;

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .readTimeout(Duration.ofMillis(readTimeout))
                .connectTimeout(Duration.ofMillis(connectTimeout))
                .build();
    }
}
