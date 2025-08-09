package org.evernet.repository;

import org.evernet.model.StorageBucketSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageBucketSchemaRepository extends JpaRepository<StorageBucketSchema, String> {

    boolean existsByStorageBucketIdentifierAndStorageSchemaAddress(String storageBucketIdentifier, String storageSchemaAddress);

    List<StorageBucketSchema> findByStorageBucketIdentifier(String storageBucketIdentifier);

    StorageBucketSchema findByStorageBucketIdentifierAndStorageSchemaAddress(String storageBucketIdentifier, String storageSchemaAddress);
}
