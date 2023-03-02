package org.evernet.app.controller;

import jakarta.validation.Valid;
import org.evernet.app.model.DataFormat;
import org.evernet.app.pojo.DataFormatCreationRequest;
import org.evernet.app.pojo.DataFormatUpdateRequest;
import org.evernet.app.service.DataFormatService;
import org.evernet.identity.authenticator.AuthenticatedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/apps/data")
public class DataFormatController extends AuthenticatedController {

    private final DataFormatService dataFormatService;

    @Autowired
    public DataFormatController(DataFormatService dataFormatService) {
        this.dataFormatService = dataFormatService;
    }

    @PostMapping(value = "/formats")
    public DataFormat register(@Valid @RequestBody DataFormatCreationRequest dataFormatCreationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return dataFormatService.register(dataFormatCreationRequest, getKnownEntity());
    }

    @DeleteMapping(value = "/formats/{identifier}")
    public DataFormat delete(@PathVariable String identifier) throws Throwable {
        return dataFormatService.delete(identifier, getKnownEntity());
    }

    @GetMapping(value = "/formats/{identifier}")
    public DataFormat get(@PathVariable String identifier) throws Throwable {
        return dataFormatService.get(identifier, getKnownEntity());
    }

    @GetMapping(value = "/formats")
    public List<DataFormat> list(Pageable pageable) {
        return dataFormatService.list(getKnownEntity(), pageable);
    }

    @PutMapping(value = "/formats/{identifier}")
    public DataFormat update(@PathVariable String identifier, @Valid @RequestBody DataFormatUpdateRequest dataFormatUpdateRequest) throws Throwable {
        return dataFormatService.update(identifier, dataFormatUpdateRequest, getKnownEntity());
    }
}
