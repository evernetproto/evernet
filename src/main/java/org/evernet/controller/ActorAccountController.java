package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.model.Actor;
import org.evernet.request.ActorSignUpRequest;
import org.evernet.request.ActorTokenRequest;
import org.evernet.response.ActorTokenResponse;
import org.evernet.service.ActorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class ActorAccountController {

    private final ActorService actorService;

    @PostMapping("/actors/signup")
    public Actor signUp(@PathVariable String nodeIdentifier, @Valid @RequestBody ActorSignUpRequest request) {
        return actorService.signUp(nodeIdentifier, request);
    }

    @PostMapping("/actors/token")
    public ActorTokenResponse getToken(@PathVariable String nodeIdentifier, @Valid @RequestBody ActorTokenRequest request) throws Exception {
        return actorService.getToken(nodeIdentifier, request);
    }
}
