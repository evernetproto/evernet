package org.evernet.messaging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.messaging.model.ConnectionGroup;
import org.evernet.messaging.request.ConnectionGroupCreationRequest;
import org.evernet.messaging.request.ConnectionGroupUpdateRequest;
import org.evernet.messaging.service.ConnectionGroupService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class ConnectionGroupController extends AuthenticatedActorController {

    private final ConnectionGroupService connectionGroupService;

    @PostMapping("/connection-groups")
    public ConnectionGroup create(@Valid @RequestBody ConnectionGroupCreationRequest request) {
        return connectionGroupService.create(request, getActorReference(), getTargetNode());
    }

    @GetMapping("/connection-groups")
    public List<ConnectionGroup> list(Pageable pageable) {
        return connectionGroupService.list(getActorReference(), getTargetNode(), pageable);
    }

    @GetMapping("/connection-groups/{identifier}")
    public ConnectionGroup get(@PathVariable String identifier) {
        return connectionGroupService.get(identifier, getActorReference(), getTargetNode());
    }

    @PutMapping("/connection-groups/{identifier}")
    public ConnectionGroup update(@PathVariable String identifier, @Valid @RequestBody ConnectionGroupUpdateRequest request) {
        return connectionGroupService.update(identifier, request, getActorReference(), getTargetNode());
    }

    @DeleteMapping("/connection-groups/{identifier}")
    public ConnectionGroup delete(@PathVariable String identifier) {
        return connectionGroupService.delete(identifier, getActorReference(), getTargetNode());
    }
}
