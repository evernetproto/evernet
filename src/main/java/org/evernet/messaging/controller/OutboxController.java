package org.evernet.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.messaging.model.Outbox;
import org.evernet.messaging.request.OutboxCreationRequest;
import org.evernet.messaging.request.OutboxUpdateRequest;
import org.evernet.messaging.service.OutboxService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class OutboxController extends AuthenticatedActorController {

    private final OutboxService outboxService;

    @PostMapping("/outboxes")
    public Outbox create(@Valid @RequestBody OutboxCreationRequest request) {
        return outboxService.create(request, getActorReference(), getTargetNode());
    }

    @GetMapping("/outboxes")
    public List<Outbox> list(Pageable pageable) {
        return outboxService.list(getActorReference(), getTargetNode(), pageable);
    }

    @GetMapping("/outboxes/{identifier}")
    public Outbox get(@PathVariable String identifier) {
        return outboxService.get(identifier, getActorReference(), getTargetNode());
    }

    @PutMapping("/outboxes/{identifier}")
    public Outbox update(@PathVariable String identifier, @Valid @RequestBody OutboxUpdateRequest request) {
        return outboxService.update(identifier, request, getActorReference(), getTargetNode());
    }

    @DeleteMapping("/outboxes/{identifier}")
    public Outbox delete(@PathVariable String identifier) {
        return outboxService.delete(identifier, getActorReference(), getTargetNode());
    }
}
