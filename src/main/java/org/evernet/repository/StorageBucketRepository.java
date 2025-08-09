package org.evernet.repository;

import org.evernet.model.StorageBucket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageBucketRepository extends JpaRepository<StorageBucket, String> {

    boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    List<StorageBucket> findByActorAddressAndNodeIdentifier(String actorAddress, String nodeIdentifier, Pageable pageable);

    StorageBucket findByIdentifierAndNodeIdentifierAndActorAddress(String identifier, String nodeIdentifier, String actorAddress);
}
