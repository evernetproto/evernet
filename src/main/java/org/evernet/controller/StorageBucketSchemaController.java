package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.model.StorageBucketSchema;
import org.evernet.request.StorageBucketSchemaRequest;
import org.evernet.service.StorageBucketSchemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/storage/buckets/{bucketIdentifier}")
@RequiredArgsConstructor
public class StorageBucketSchemaController extends AuthenticatedActorController {

    private final StorageBucketSchemaService storageBucketSchemaService;

    @PostMapping("/schemas")
    public StorageBucketSchema create(@PathVariable String bucketIdentifier, @Valid @RequestBody StorageBucketSchemaRequest request) {
        return storageBucketSchemaService.create(bucketIdentifier, request, getTargetNodeIdentifier(), getActorAddress(), null);
    }

    @GetMapping("/schemas")
    public List<StorageBucketSchema> list(@PathVariable String bucketIdentifier) {
        return storageBucketSchemaService.list(bucketIdentifier, getTargetNodeIdentifier(), getActorAddress());
    }

    @DeleteMapping("/schemas")
    public StorageBucketSchema delete(@PathVariable String bucketIdentifier, @Valid @RequestBody StorageBucketSchemaRequest request) {
        return storageBucketSchemaService.delete(bucketIdentifier, request, getTargetNodeIdentifier(), getActorAddress());
    }
}
