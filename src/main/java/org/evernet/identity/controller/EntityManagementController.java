package org.evernet.identity.controller;

import jakarta.validation.Valid;
import org.evernet.identity.authenticator.AuthenticatedController;
import org.evernet.identity.model.Entity;
import org.evernet.identity.pojo.EntityUpdateRequest;
import org.evernet.identity.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/identity")
public class EntityManagementController extends AuthenticatedController {

    private final EntityService entityService;

    @Autowired
    public EntityManagementController(EntityService entityService) {
        this.entityService = entityService;
    }

    @GetMapping(value = "/entities/current")
    public Entity get() throws Throwable {
        return entityService.getByPublicKey(getPublicKey());
    }

    @PutMapping(value = "/entities/current")
    public Entity update(@Valid @RequestBody EntityUpdateRequest entityUpdateRequest) throws Throwable {
        return entityService.update(getPublicKey(), entityUpdateRequest);
    }

    @DeleteMapping(value = "/entities/current")
    public Entity delete() throws Throwable {
        return entityService.delete(getPublicKey());
    }
}
