package org.evernet.acl.service;

import org.evernet.acl.enums.TargetType;
import org.evernet.acl.model.Access;
import org.evernet.acl.repository.AccessRepository;
import org.evernet.common.exception.ClientException;
import org.evernet.common.exception.NotFoundException;
import org.evernet.identity.model.KnownEntity;
import org.evernet.identity.pojo.EntityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.function.Supplier;

@Service
public class AccessService {

    private final AccessRepository accessRepository;

    private final MongoTemplate mongoTemplate;

    private final String nodeEndpoint;

    @Autowired
    public AccessService(AccessRepository accessRepository, MongoTemplate mongoTemplate,
                         @Value("${evernet.node.endpoint}") String nodeEndpoint) {
        this.accessRepository = accessRepository;
        this.mongoTemplate = mongoTemplate;
        this.nodeEndpoint = nodeEndpoint;
    }

    public void save(TargetType targetType, String targetIdentifier, KnownEntity entity, String creatorId) throws InvalidKeySpecException, NoSuchAlgorithmException {
        save(targetType, targetIdentifier, EntityDetails.fromKnownEntity(entity, nodeEndpoint), creatorId);
    }

    public Access save(TargetType targetType, String targetIdentifier, EntityDetails entity, String creatorId) {
        Access access = new Access(targetType, targetIdentifier, entity, creatorId);
        return accessRepository.save(access);
    }

    public Access add(TargetType targetType, String targetIdentifier, EntityDetails entity, String creatorId) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (hasAccess(targetType, targetIdentifier, entity.getPublicKey().getString())) {
            throw new ClientException("Access already exists");
        }

        return save(targetType, targetIdentifier, entity, creatorId);
    }

    public Access delete(TargetType targetType, String targetIdentifier, String entityPublicKey) throws Throwable {
        Access access = accessRepository.findByTargetTypeAndTargetIdentifierAndEntityPublicKeyString(targetType, targetIdentifier, entityPublicKey)
                .orElseThrow((Supplier<Throwable>) () -> new NotFoundException("Access details not found"));

        accessRepository.delete(access);
        return access;
    }

    public void deleteAll(TargetType targetType, String targetIdentifier) {
        mongoTemplate.remove(Query.query(Criteria
                .where("targetType").is(targetType)
                .and("targetIdentifier").is(targetIdentifier)), Access.class);
    }

    public List<Access> list(TargetType targetType, String targetIdentifier, Pageable pageable) {
        return accessRepository.findByTargetTypeAndTargetIdentifier(targetType, targetIdentifier, pageable);
    }

    public List<Access> listTargets(TargetType targetType, String entityPublicKey, Pageable pageable) {
        return accessRepository.findByTargetTypeAndEntityPublicKeyString(targetType, entityPublicKey, pageable);
    }

    public Boolean hasAccess(TargetType targetType, String targetIdentifier, String entityPublicKey) {
        return accessRepository.existsByTargetTypeAndTargetIdentifierAndEntityPublicKeyString(targetType, targetIdentifier, entityPublicKey);
    }
}
