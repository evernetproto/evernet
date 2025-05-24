package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Actor;
import org.evernet.request.ActorAdditionRequest;
import org.evernet.request.ActorUpdateRequest;
import org.evernet.response.ActorPasswordResponse;
import org.evernet.service.ActorService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class ActorAdminController extends AuthenticatedAdminController {

    private final ActorService actorService;

    @PostMapping("/nodes/{nodeIdentifier}/actors")
    public ActorPasswordResponse add(@PathVariable String nodeIdentifier, @Valid @RequestBody ActorAdditionRequest request) {
        return actorService.add(nodeIdentifier, request, getAdminIdentifier());
    }

    @GetMapping("/nodes/{nodeIdentifier}/actors")
    public List<Actor> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return actorService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/nodes/{nodeIdentifier}/actors/{actorIdentifier}")
    public Actor get(@PathVariable String nodeIdentifier, @PathVariable String actorIdentifier) {
        return actorService.get(actorIdentifier, nodeIdentifier);
    }

    @PutMapping("/nodes/{nodeIdentifier}/actors/{actorIdentifier}")
    public Actor update(@PathVariable String nodeIdentifier, @PathVariable String actorIdentifier, @Valid @RequestBody ActorUpdateRequest request) {
        return actorService.update(actorIdentifier, request, nodeIdentifier);
    }

    @DeleteMapping("/nodes/{nodeIdentifier}/actors/{actorIdentifier}")
    public Actor delete(@PathVariable String nodeIdentifier, @PathVariable String actorIdentifier) {
        return actorService.delete(actorIdentifier, nodeIdentifier);
    }

    @PutMapping("/nodes/{nodeIdentifier}/actors/{actorIdentifier}/password")
    public ActorPasswordResponse resetPassword(@PathVariable String nodeIdentifier, @PathVariable String actorIdentifier) {
        return actorService.resetPassword(actorIdentifier, nodeIdentifier);
    }
}
