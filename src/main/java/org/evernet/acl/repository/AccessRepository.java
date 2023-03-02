package org.evernet.acl.repository;

import org.evernet.acl.enums.TargetType;
import org.evernet.acl.model.Access;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRepository extends MongoRepository<Access, String> {

    Optional<Access> findByTargetTypeAndTargetIdentifierAndEntityPublicKeyString(TargetType targetType, String targetIdentifier, String entityPublicKey);

    List<Access> findByTargetTypeAndTargetIdentifier(TargetType targetType, String targetIdentifier, Pageable pageable);

    List<Access> findByTargetTypeAndEntityPublicKeyString(TargetType targetType, String entityPublicKey, Pageable pageable);

    Boolean existsByTargetTypeAndTargetIdentifierAndEntityPublicKeyString(TargetType targetType, String targetIdentifier, String entityPublicKey);
}
