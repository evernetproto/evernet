package org.evernet.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.messaging.model.ConnectionGroup;
import org.evernet.messaging.request.ConnectionGroupCreationRequest;
import org.evernet.messaging.service.ConnectionGroupService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class ConnectionGroupController extends AuthenticatedActorController {

    private final ConnectionGroupService connectionGroupService;

    @PostMapping("/connection-groups")
    public ConnectionGroup create(@Valid @RequestBody ConnectionGroupCreationRequest request) {
        return connectionGroupService.create(request, getActorReference(), getTargetNode());
    }
}
