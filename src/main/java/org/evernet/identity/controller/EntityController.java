package org.evernet.identity.controller;

import jakarta.validation.Valid;
import org.evernet.identity.model.Entity;
import org.evernet.identity.pojo.EntityRegistrationRequest;
import org.evernet.identity.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/identity")
public class EntityController {

    private final EntityService entityService;

    @Autowired
    public EntityController(EntityService entityService) {
        this.entityService = entityService;
    }

    @PostMapping(value = "/entities")
    public Entity register(@Valid @RequestBody EntityRegistrationRequest entityRegistrationRequest) {
        return entityService.register(entityRegistrationRequest);
    }

    @GetMapping(value = "/entities/{identifier}/public-key", produces = "text/plain")
    public String getPublicKey(@PathVariable String identifier) throws Throwable {
        return entityService.getPublicKey(identifier);
    }

    @GetMapping(value = "/entities/{identifier}")
    public Entity get(@PathVariable String identifier) throws Throwable {
        return entityService.get(identifier);
    }
}
