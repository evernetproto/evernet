package xyz.evernet.vertex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.vertex.response.HealthCheckResponse;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("/health")
    public HealthCheckResponse healthCheck() {
        return new HealthCheckResponse("OK");
    }
}
