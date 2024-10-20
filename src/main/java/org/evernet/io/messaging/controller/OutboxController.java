package org.evernet.io.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedActorController;
import org.evernet.io.messaging.model.Outbox;
import org.evernet.io.messaging.request.OutboxCreationRequest;
import org.evernet.io.messaging.request.OutboxUpdateRequest;
import org.evernet.io.messaging.service.OutboxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class OutboxController extends AuthenticatedActorController {

    private final OutboxService outboxService;

    @PostMapping("/outboxes")
    public Outbox create(@Valid @RequestBody OutboxCreationRequest request) {
        return outboxService.create(request, getActor());
    }

    @GetMapping("/outboxes")
    public Page<Outbox> list(Pageable pageable) {
        return outboxService.list(getActorAddress(), pageable);
    }

    @GetMapping("/outboxes/{identifier}")
    public Outbox get(@PathVariable String identifier) {
        return outboxService.get(identifier, getActor());
    }

    @PutMapping("/outboxes/{identifier}")
    public Outbox update(@PathVariable String identifier, @Valid @RequestBody OutboxUpdateRequest request) {
        return outboxService.update(identifier, request, getActor());
    }

    @DeleteMapping("/outboxes/{identifier}")
    public Outbox delete(@PathVariable String identifier) {
        return outboxService.delete(identifier, getActor());
    }
}
