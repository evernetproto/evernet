package org.evernet.actor.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.actor.model.Actor;
import org.evernet.actor.service.ActorService;
import org.evernet.auth.AuthenticatedActorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActorController extends AuthenticatedActorController {

    private final ActorService actorService;

    @GetMapping("/actors/current")
    public Actor get() {
        checkLocalActor();
        return actorService.get(getActorReference().getIdentifier(), getTargetNode().getIdentifier());
    }
}
