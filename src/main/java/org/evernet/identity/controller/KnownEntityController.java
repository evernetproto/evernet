package org.evernet.identity.controller;

import org.evernet.identity.authenticator.AuthenticatedController;
import org.evernet.identity.model.KnownEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/identity")
public class KnownEntityController extends AuthenticatedController {

    @GetMapping(value = "/entities/known/current")
    public KnownEntity get() {
        return getKnownEntity();
    }
}
