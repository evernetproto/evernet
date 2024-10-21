package org.evernet.io.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedActorController;
import org.evernet.io.messaging.model.Inbox;
import org.evernet.io.messaging.request.InboxCreationRequest;
import org.evernet.io.messaging.request.InboxUpdateRequest;
import org.evernet.io.messaging.service.InboxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class InboxController extends AuthenticatedActorController {

    private final InboxService inboxService;

    @PostMapping("/inboxes")
    public Inbox create(@Valid @RequestBody InboxCreationRequest request) {
        return inboxService.create(request, getActor());
    }

    @GetMapping("/inboxes")
    public Page<Inbox> list(Pageable pageable) {
        return inboxService.list(getActor(), pageable);
    }

    @GetMapping("/inboxes/{identifier}")
    public Inbox get(@PathVariable String identifier) {
        return inboxService.get(identifier, getActor());
    }

    @PutMapping("/inboxes/{identifier}")
    public Inbox update(@PathVariable String identifier, @Valid @RequestBody InboxUpdateRequest request) {
        return inboxService.update(identifier, request, getActor());
    }

    @DeleteMapping("/inboxes/{identifier}")
    public Inbox delete(@PathVariable String identifier) {
        return inboxService.delete(identifier, getActor());
    }
}
