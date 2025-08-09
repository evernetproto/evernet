package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.model.StorageBucket;
import org.evernet.model.StorageBucketSchema;
import org.evernet.repository.StorageBucketSchemaRepository;
import org.evernet.request.StorageBucketSchemaRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageBucketSchemaService {

    private final StorageBucketSchemaRepository storageBucketSchemaRepository;

    private final StorageBucketService storageBucketService;

    public StorageBucketSchema create(String storageBucketIdentifier, StorageBucketSchemaRequest request, String nodeIdentifier, String actorAddress, String creator) {
        StorageBucket storageBucket = storageBucketService.get(storageBucketIdentifier, nodeIdentifier, actorAddress);

        if (storageBucketSchemaRepository.existsByStorageBucketIdentifierAndStorageSchemaAddress(storageBucket.getIdentifier(), request.getStorageSchemaAddress())) {
            throw new ClientException(String.format("Storage schema %s already added to storage bucket %s", request.getStorageSchemaAddress(), storageBucket.getIdentifier()));
        }

        StorageBucketSchema storageBucketSchema = StorageBucketSchema.builder()
                .storageBucketIdentifier(storageBucket.getIdentifier())
                .storageSchemaAddress(request.getStorageSchemaAddress())
                .creator(creator)
                .build();

        return storageBucketSchemaRepository.save(storageBucketSchema);
    }

    public List<StorageBucketSchema> list(String storageBucketIdentifier, String nodeIdentifier, String actorAddress) {
        StorageBucket storageBucket = storageBucketService.get(storageBucketIdentifier, nodeIdentifier, actorAddress);
        return storageBucketSchemaRepository.findByStorageBucketIdentifier(storageBucket.getIdentifier());
    }

    public StorageBucketSchema delete(String storageBucketIdentifier, StorageBucketSchemaRequest request, String nodeIdentifier, String actorAddress) {
        StorageBucket storageBucket = storageBucketService.get(storageBucketIdentifier, nodeIdentifier, actorAddress);
        StorageBucketSchema storageBucketSchema = storageBucketSchemaRepository.findByStorageBucketIdentifierAndStorageSchemaAddress(storageBucket.getIdentifier(), request.getStorageSchemaAddress());
        storageBucketSchemaRepository.delete(storageBucketSchema);
        return storageBucketSchema;
    }
}
