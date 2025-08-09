package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.StorageSchema;
import org.evernet.request.StorageSchemaCloneRequest;
import org.evernet.request.StorageSchemaCreationRequest;
import org.evernet.service.StorageSchemaService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/storage")
@RequiredArgsConstructor
public class StorageSchemaAdminController extends AuthenticatedAdminController {

    private final StorageSchemaService storageSchemaService;

    @PostMapping("/schemas")
    public StorageSchema create(@PathVariable String nodeIdentifier, @Valid @RequestBody StorageSchemaCreationRequest request) {
        return storageSchemaService.create(
                nodeIdentifier,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/schemas")
    public List<StorageSchema> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return storageSchemaService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/schema")
    public StorageSchema get(@PathVariable String nodeIdentifier, @RequestParam String address) {
        return storageSchemaService.get(nodeIdentifier, address);
    }

    @DeleteMapping("/schema")
    public StorageSchema delete(@PathVariable String nodeIdentifier, @RequestParam String address) {
        return storageSchemaService.delete(nodeIdentifier, address);
    }

    @PostMapping("/schemas/clone")
    public StorageSchema copy(@PathVariable String nodeIdentifier, @Valid @RequestBody StorageSchemaCloneRequest request) {
        return storageSchemaService.copy(nodeIdentifier, request.getAddress(), getAdminIdentifier());
    }
}
