package org.evernet.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.messaging.enums.ConnectionType;
import org.evernet.messaging.model.Connection;
import org.evernet.messaging.request.ConnectionRequest;
import org.evernet.messaging.service.ConnectionService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class ConnectionController extends AuthenticatedActorController {

    private final ConnectionService connectionService;

    @PostMapping("/connection-groups/{connectionGroupIdentifier}/connections")
    public Connection create(@PathVariable String connectionGroupIdentifier, @Valid @RequestBody ConnectionRequest request) {
        return connectionService.create(connectionGroupIdentifier, request, getActorReference(), getTargetNode());
    }

    @GetMapping("/connection-groups/{connectionGroupIdentifier}/connections")
    public List<Connection> list(@PathVariable String connectionGroupIdentifier, @RequestParam ConnectionType type, Pageable pageable) {
        return connectionService.list(connectionGroupIdentifier, type, getActorReference(), getTargetNode(), pageable);
    }

    @DeleteMapping("/connection-groups/{connectionGroupIdentifier}/connections")
    public Connection delete(@PathVariable String connectionGroupIdentifier, @Valid @RequestBody ConnectionRequest request) {
        return connectionService.delete(connectionGroupIdentifier, request.getType(), request.getAddress(), getActorReference(), getTargetNode());
    }
}
