package org.evernet.app.controller;

import jakarta.validation.Valid;
import org.evernet.acl.model.Access;
import org.evernet.app.model.App;
import org.evernet.app.pojo.AppCreationRequest;
import org.evernet.app.pojo.AppManagerRequest;
import org.evernet.app.pojo.AppUpdateRequest;
import org.evernet.app.service.AppService;
import org.evernet.identity.authenticator.AuthenticatedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class AppController extends AuthenticatedController {

    private final AppService appService;

    @Autowired
    public AppController(AppService appService) {
        this.appService = appService;
    }

    @PostMapping(value = "/apps")
    public App create(@Valid @RequestBody AppCreationRequest appCreationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return appService.create(appCreationRequest, getKnownEntity());
    }

    @GetMapping(value = "/apps")
    public List<App> list(Pageable pageable) {
        return appService.list(getKnownEntity(), pageable);
    }

    @GetMapping(value = "/apps/{identifier}")
    public App get(@PathVariable String identifier) throws Throwable {
        return appService.get(identifier, getKnownEntity());
    }

    @PutMapping(value = "/apps/{identifier}")
    public App update(@PathVariable String identifier, @Valid @RequestBody AppUpdateRequest appUpdateRequest) throws Throwable {
        return appService.update(identifier, appUpdateRequest, getKnownEntity());
    }

    @DeleteMapping(value = "/apps/{identifier}")
    public App delete(@PathVariable String identifier) throws Throwable {
        return appService.delete(identifier, getKnownEntity());
    }

    @PostMapping(value = "/apps/{identifier}/publish")
    public App publish(@PathVariable String identifier) throws Throwable {
        return appService.publish(identifier, getKnownEntity());
    }

    @DeleteMapping(value = "/apps/{identifier}/publish")
    public App unPublish(@PathVariable String identifier) throws Throwable {
        return appService.unPublish(identifier, getKnownEntity());
    }

    @PostMapping(value = "/apps/{identifier}/managers")
    public Access addManager(@PathVariable String identifier, @Valid @RequestBody AppManagerRequest appManagerRequest) throws Throwable {
        return appService.addManager(identifier, appManagerRequest, getKnownEntity());
    }

    @DeleteMapping(value = "/apps/{identifier}/managers")
    public Access deleteManager(@PathVariable String identifier, @Valid @RequestBody AppManagerRequest appManagerRequest) throws Throwable {
        return appService.deleteManager(identifier, appManagerRequest, getKnownEntity());
    }

    @GetMapping(value = "/apps/{identifier}/managers")
    public List<Access> listManagers(@PathVariable String identifier, Pageable pageable) {
        return appService.listManagers(identifier, getKnownEntity(), pageable);
    }
}
