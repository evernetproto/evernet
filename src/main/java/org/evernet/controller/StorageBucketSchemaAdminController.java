package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.StorageBucketSchema;
import org.evernet.request.StorageBucketSchemaRequest;
import org.evernet.service.StorageBucketSchemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/storage/buckets/{bucketIdentifier}")
@RequiredArgsConstructor
public class StorageBucketSchemaAdminController extends AuthenticatedAdminController {

    private final StorageBucketSchemaService storageBucketSchemaService;

    @PostMapping("/schemas")
    public StorageBucketSchema create(@PathVariable String nodeIdentifier, @PathVariable String bucketIdentifier, @Valid @RequestBody StorageBucketSchemaRequest request, @RequestParam String actorAddress) {
        return storageBucketSchemaService.create(
                bucketIdentifier,
                request,
                nodeIdentifier,
                actorAddress,
                getAdminIdentifier()
        );
    }

    @GetMapping("/schemas")
    public List<StorageBucketSchema> list(@PathVariable String nodeIdentifier, @PathVariable String bucketIdentifier, @RequestParam String actorAddress) {
        return storageBucketSchemaService.list(
                bucketIdentifier,
                nodeIdentifier,
                actorAddress
        );
    }

    @DeleteMapping("/schemas")
    public StorageBucketSchema delete(@PathVariable String nodeIdentifier, @PathVariable String bucketIdentifier, @Valid @RequestBody StorageBucketSchemaRequest request, @RequestParam String actorAddress) {
        return storageBucketSchemaService.delete(
                bucketIdentifier,
                request,
                nodeIdentifier,
                actorAddress
        );
    }
}
