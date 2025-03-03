package org.evernet.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.messaging.model.Inbox;
import org.evernet.messaging.request.InboxCreationRequest;
import org.evernet.messaging.service.InboxService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class InboxController extends AuthenticatedActorController {

    private final InboxService inboxService;

    @PostMapping("/inboxes")
    public Inbox create(@Valid @RequestBody InboxCreationRequest request) {
        return inboxService.create(request, getTargetNode(), getActorReference());
    }

    @GetMapping("/inboxes")
    public List<Inbox> list(Pageable pageable) {
        return inboxService.list(getActorReference(), getTargetNode(), pageable);
    }
}
