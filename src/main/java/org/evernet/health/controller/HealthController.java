package org.evernet.health.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.health.response.HealthCheckResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    @GetMapping("/health")
    public HealthCheckResponse healthCheck() {
        return HealthCheckResponse.builder().status("ok").build();
    }
}
