package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.model.StorageBucket;
import org.evernet.request.StorageBucketCreationRequest;
import org.evernet.request.StorageBucketUpdateRequest;
import org.evernet.service.StorageBucketService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageBucketController extends AuthenticatedActorController {

    private final StorageBucketService storageBucketService;

    @PostMapping("/buckets")
    public StorageBucket create(@Valid @RequestBody StorageBucketCreationRequest request) {
        return storageBucketService.create(
                request,
                getTargetNodeIdentifier(),
                getActorAddress(),
                null
        );
    }

    @GetMapping("/buckets")
    public List<StorageBucket> list(Pageable pageable) {
        return storageBucketService.list(getActorAddress(), getTargetNodeIdentifier(), pageable);
    }

    @GetMapping("/buckets/{identifier}")
    public StorageBucket get(@PathVariable String identifier) {
        return storageBucketService.get(identifier, getTargetNodeIdentifier(), getActorAddress());
    }

    @PutMapping("/buckets/{identifier}")
    public StorageBucket update(@PathVariable String identifier, @Valid @RequestBody StorageBucketUpdateRequest request) {
        return storageBucketService.update(
                identifier,
                request,
                getTargetNodeIdentifier(),
                getActorAddress()
        );
    }

    @DeleteMapping("/buckets/{identifier}")
    public StorageBucket delete(@PathVariable String identifier) {
        return storageBucketService.delete(identifier, getTargetNodeIdentifier(), getActorAddress());
    }
}
