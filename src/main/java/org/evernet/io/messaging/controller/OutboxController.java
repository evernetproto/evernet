package org.evernet.io.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedActorController;
import org.evernet.io.messaging.model.Outbox;
import org.evernet.io.messaging.request.OutboxCreationRequest;
import org.evernet.io.messaging.service.OutboxService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class OutboxController extends AuthenticatedActorController {

    private final OutboxService outboxService;

    @PostMapping("/outboxes")
    public Outbox create(@Valid @RequestBody OutboxCreationRequest request) {
        return outboxService.create(request, getActor());
    }
}
