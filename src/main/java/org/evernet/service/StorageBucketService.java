package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.StorageBucket;
import org.evernet.repository.StorageBucketRepository;
import org.evernet.request.StorageBucketCreationRequest;
import org.evernet.request.StorageBucketUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageBucketService {

    private final StorageBucketRepository storageBucketRepository;

    private final NodeService nodeService;

    public StorageBucket create(StorageBucketCreationRequest request, String nodeIdentifier, String actorAddress, String creator) {
        if (StringUtils.hasText(creator)) {
            if (!nodeService.exists(nodeIdentifier)) {
                throw new ClientException(String.format("Node %s not found", nodeIdentifier));
            }
        }

        if (storageBucketRepository.existsByIdentifierAndNodeIdentifier(request.getIdentifier(), nodeIdentifier)) {
            throw new ClientException(String.format("Storage bucket %s already exists on node %s", request.getIdentifier(), nodeIdentifier));
        }

        StorageBucket storageBucket = StorageBucket.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .nodeIdentifier(nodeIdentifier)
                .actorAddress(actorAddress)
                .creator(creator)
                .build();

        return storageBucketRepository.save(storageBucket);
    }

    public List<StorageBucket> list(String actorAddress, String nodeIdentifier, Pageable pageable) {
        return storageBucketRepository.findByActorAddressAndNodeIdentifier(actorAddress, nodeIdentifier, pageable);
    }

    public StorageBucket get(String identifier, String nodeIdentifier, String actorAddress) {
        StorageBucket storageBucket = storageBucketRepository.findByIdentifierAndNodeIdentifierAndActorAddress(
                identifier,
                nodeIdentifier,
                actorAddress
        );

        if (storageBucket == null) {
            throw new NotFoundException(String.format("Storage bucket %s not found for actor %s on node %s", identifier, actorAddress, nodeIdentifier));
        }

        return storageBucket;
    }

    public StorageBucket update(String identifier, StorageBucketUpdateRequest request, String nodeIdentifier, String actorAddress) {
        StorageBucket storageBucket = get(identifier, nodeIdentifier, actorAddress);

        if (StringUtils.hasText(request.getDisplayName())) {
            storageBucket.setDisplayName(request.getDisplayName());
        }

        storageBucket.setDescription(request.getDescription());

        return storageBucketRepository.save(storageBucket);
    }

    public StorageBucket delete(String identifier, String nodeIdentifier, String actorAddress) {
        StorageBucket storageBucket = get(identifier, nodeIdentifier, actorAddress);
        storageBucketRepository.delete(storageBucket);
        return storageBucket;
    }
}
