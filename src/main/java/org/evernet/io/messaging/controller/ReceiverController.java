package org.evernet.io.messaging.controller;

import jakarta.validation.Valid;
import org.evernet.acl.model.Access;
import org.evernet.identity.authenticator.AuthenticatedController;
import org.evernet.io.messaging.model.Receiver;
import org.evernet.io.messaging.pojo.ReceiverCreationRequest;
import org.evernet.io.messaging.pojo.ReceiverAccessRequest;
import org.evernet.io.messaging.pojo.ReceiverUpdateRequest;
import org.evernet.io.messaging.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/io/messaging")
public class ReceiverController extends AuthenticatedController {

    private final ReceiverService receiverService;

    @Autowired
    public ReceiverController(ReceiverService receiverService) {
        this.receiverService = receiverService;
    }

    @PostMapping(value = "/receivers")
    public Receiver create(@Valid @RequestBody ReceiverCreationRequest receiverCreationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return receiverService.create(receiverCreationRequest, getKnownEntity());
    }

    @GetMapping(value = "/receivers")
    public List<Receiver> list(Pageable pageable) {
        return receiverService.list(getKnownEntity(), pageable);
    }

    @GetMapping(value = "/receivers/{identifier}")
    public Receiver get(@PathVariable String identifier) throws Throwable {
        return receiverService.get(identifier, getKnownEntity());
    }

    @PutMapping(value = "/receivers/{identifier}")
    public Receiver update(@PathVariable String identifier, @Valid @RequestBody ReceiverUpdateRequest receiverUpdateRequest) throws Throwable {
        return receiverService.update(identifier, receiverUpdateRequest, getKnownEntity());
    }

    @DeleteMapping(value = "/receivers/{identifier}")
    public Receiver delete(@PathVariable String identifier) throws Throwable {
        return receiverService.delete(identifier, getKnownEntity());
    }

    @PostMapping(value = "/receivers/{identifier}/access")
    public Access addAccess(@PathVariable String identifier, @Valid @RequestBody ReceiverAccessRequest receiverAccessRequest) throws Throwable {
        return receiverService.addAccess(identifier, receiverAccessRequest, getKnownEntity());
    }

    @DeleteMapping(value = "/receivers/{identifier}/access")
    public Access deleteAccess(@PathVariable String identifier, @Valid @RequestBody ReceiverAccessRequest receiverAccessRequest) throws Throwable {
        return receiverService.deleteAccess(identifier, receiverAccessRequest, getKnownEntity());
    }

    @GetMapping(value = "/receivers/{identifier}/access")
    public List<Access> listAccess(@PathVariable String identifier, Pageable pageable) {
        return receiverService.listAccess(identifier, getKnownEntity(), pageable);
    }
}
