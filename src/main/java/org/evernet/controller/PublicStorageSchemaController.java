package org.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.model.StorageSchema;
import org.evernet.service.StorageSchemaService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes/{nodeIdentifier}/storage")
@RequiredArgsConstructor
public class PublicStorageSchemaController {

    private final StorageSchemaService storageSchemaService;

    @GetMapping("/schemas")
    public List<StorageSchema> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return storageSchemaService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/type")
    public StorageSchema get(@PathVariable String nodeIdentifier, @RequestParam String address) {
        return storageSchemaService.get(nodeIdentifier, address);
    }
}
