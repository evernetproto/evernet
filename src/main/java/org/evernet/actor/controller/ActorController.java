package org.evernet.actor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.actor.model.Actor;
import org.evernet.actor.request.ActorPasswordChangeRequest;
import org.evernet.actor.request.ActorUpdateRequest;
import org.evernet.actor.service.ActorService;
import org.evernet.core.auth.AuthenticatedActorController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActorController extends AuthenticatedActorController {

    private final ActorService actorService;

    @GetMapping("/actors/current")
    public Actor get() {
        checkLocalActor();
        return actorService.get(getActorIdentifier(), getTargetNodeIdentifier());
    }

    @PutMapping("/actors/current")
    public Actor update(@Valid @RequestBody ActorUpdateRequest request) {
        checkLocalActor();
        return actorService.update(getActorIdentifier(), request, getTargetNodeIdentifier());
    }

    @PutMapping("/actors/current/password")
    public Actor changePassword(@Valid @RequestBody ActorPasswordChangeRequest request) {
        checkLocalActor();
        return actorService.changePassword(getActorIdentifier(), request, getTargetNodeIdentifier());
    }

    @DeleteMapping("/actors/current")
    public Actor delete() {
        checkLocalActor();
        return actorService.delete(getActorIdentifier(), getTargetNodeIdentifier());
    }
}
