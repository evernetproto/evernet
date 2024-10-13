package org.evernet.actor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.actor.model.Actor;
import org.evernet.actor.request.ActorSignUpRequest;
import org.evernet.actor.service.ActorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActorAccountController {

    private final ActorService actorService;

    @PostMapping("/actors/signup")
    public Actor signUp(@Valid @RequestBody ActorSignUpRequest request) {
        return actorService.signUp(request);
    }
}
