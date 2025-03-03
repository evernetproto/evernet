package org.evernet.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.messaging.model.Inbox;
import org.evernet.messaging.request.InboxCreationRequest;
import org.evernet.messaging.service.InboxService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class InboxController extends AuthenticatedActorController {

    private final InboxService inboxService;

    @PostMapping("/inboxes")
    public Inbox create(@Valid @RequestBody InboxCreationRequest request) {
        return inboxService.create(request, getTargetNode(), getActorReference());
    }
}
