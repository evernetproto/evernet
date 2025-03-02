package org.evernet.actor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.actor.model.Actor;
import org.evernet.actor.request.ActorSignUpRequest;
import org.evernet.actor.service.ActorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActorAccountController {

    private final ActorService actorService;

    @PostMapping("/nodes/{nodeIdentifier}/actors/signup")
    public Actor signUp(@PathVariable String nodeIdentifier, @Valid @RequestBody ActorSignUpRequest request) {
        return actorService.signUp(nodeIdentifier, request);
    }
}
