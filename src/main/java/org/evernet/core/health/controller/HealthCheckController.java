package org.evernet.core.health.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.core.health.response.HealthCheckResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("/health")
    public HealthCheckResponse healthCheck() {
        return HealthCheckResponse.builder().status("ok").build();
    }
}
