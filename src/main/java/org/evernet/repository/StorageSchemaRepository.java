package org.evernet.repository;

import org.evernet.model.StorageSchema;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageSchemaRepository extends JpaRepository<StorageSchema, String> {
    boolean existsByNodeIdentifierAndAddress(String nodeIdentifier, String address);

    List<StorageSchema> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);

    StorageSchema findByNodeIdentifierAndAddress(String nodeIdentifier, String address);
}
