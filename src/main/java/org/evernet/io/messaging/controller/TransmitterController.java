package org.evernet.io.messaging.controller;

import jakarta.validation.Valid;
import org.evernet.acl.model.Access;
import org.evernet.identity.authenticator.AuthenticatedController;
import org.evernet.io.messaging.model.Transmitter;
import org.evernet.io.messaging.pojo.TransmitterAccessRequest;
import org.evernet.io.messaging.pojo.TransmitterCreationRequest;
import org.evernet.io.messaging.pojo.TransmitterUpdateRequest;
import org.evernet.io.messaging.service.TransmitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/io/messaging")
public class TransmitterController extends AuthenticatedController {

    private final TransmitterService transmitterService;

    @Autowired
    public TransmitterController(TransmitterService transmitterService) {
        this.transmitterService = transmitterService;
    }

    @PostMapping(value = "/transmitters")
    public Transmitter create(@Valid @RequestBody TransmitterCreationRequest transmitterCreationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return transmitterService.create(transmitterCreationRequest, getKnownEntity());
    }

    @GetMapping(value = "/transmitters")
    public List<Transmitter> list(Pageable pageable) {
        return transmitterService.list(getKnownEntity(), pageable);
    }

    @GetMapping(value = "/transmitters/{identifier}")
    public Transmitter get(@PathVariable String identifier) throws Throwable {
        return transmitterService.get(identifier, getKnownEntity());
    }

    @PutMapping(value = "/transmitters/{identifier}")
    public Transmitter update(@PathVariable String identifier, @Valid @RequestBody TransmitterUpdateRequest transmitterUpdateRequest) throws Throwable {
        return transmitterService.update(identifier, transmitterUpdateRequest, getKnownEntity());
    }

    @DeleteMapping(value = "/transmitters/{identifier}")
    public Transmitter delete(@PathVariable String identifier) throws Throwable {
        return transmitterService.delete(identifier, getKnownEntity());
    }

    @PostMapping(value = "/transmitters/{identifier}/access")
    public Access addAccess(@PathVariable String identifier, @Valid @RequestBody TransmitterAccessRequest transmitterAccessRequest) throws Throwable {
        return transmitterService.addAccess(identifier, transmitterAccessRequest, getKnownEntity());
    }

    @DeleteMapping(value = "/transmitters/{identifier}/access")
    public Access deleteAccess(@PathVariable String identifier, @Valid @RequestBody TransmitterAccessRequest transmitterAccessRequest) throws Throwable {
        return transmitterService.deleteAccess(identifier, transmitterAccessRequest, getKnownEntity());
    }

    @GetMapping(value = "/transmitters/{identifier}/access")
    public List<Access> listAccess(@PathVariable String identifier, Pageable pageable) {
        return transmitterService.listAccess(identifier, getKnownEntity(), pageable);
    }
}
