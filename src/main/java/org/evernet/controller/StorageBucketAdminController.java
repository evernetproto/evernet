package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.StorageBucket;
import org.evernet.request.StorageBucketCreationRequest;
import org.evernet.request.StorageBucketUpdateRequest;
import org.evernet.service.StorageBucketService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/storage")
@RequiredArgsConstructor
public class StorageBucketAdminController extends AuthenticatedAdminController {

    private final StorageBucketService storageBucketService;

    @PostMapping("/buckets")
    public StorageBucket create(@PathVariable String nodeIdentifier, @Valid @RequestBody StorageBucketCreationRequest request, @RequestParam String actorAddress) {
        return storageBucketService.create(
                request,
                nodeIdentifier,
                actorAddress,
                getAdminIdentifier()
        );
    }

    @GetMapping("/buckets")
    public List<StorageBucket> list(@PathVariable String nodeIdentifier, @RequestParam String actorAddress, Pageable pageable) {
        return storageBucketService.list(actorAddress, nodeIdentifier, pageable);
    }

    @GetMapping("/buckets/{identifier}")
    public StorageBucket get(@PathVariable String nodeIdentifier, @PathVariable String identifier, @RequestParam String actorAddress) {
        return storageBucketService.get(identifier, nodeIdentifier, actorAddress);
    }

    @PutMapping("/buckets/{identifier}")
    public StorageBucket update(@PathVariable String nodeIdentifier, @PathVariable String identifier, @RequestParam String actorAddress, @Valid @RequestBody StorageBucketUpdateRequest request) {
        return storageBucketService.update(identifier, request, nodeIdentifier, actorAddress);
    }

    @DeleteMapping("/buckets/{identifier}")
    public StorageBucket delete(@PathVariable String nodeIdentifier, @PathVariable String identifier, @RequestParam String actorAddress) {
        return storageBucketService.delete(identifier, nodeIdentifier, actorAddress);
    }
}
