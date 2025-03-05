package org.evernet.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.messaging.model.Connection;
import org.evernet.messaging.request.ConnectionCreationRequest;
import org.evernet.messaging.service.ConnectionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class ConnectionController extends AuthenticatedActorController {

    private final ConnectionService connectionService;

    @PostMapping("/connection-groups/{connectionGroupIdentifier}/connections")
    public Connection create(@PathVariable String connectionGroupIdentifier, @Valid @RequestBody ConnectionCreationRequest request) {
        return connectionService.create(connectionGroupIdentifier, request, getActorReference(), getTargetNode());
    }
}
