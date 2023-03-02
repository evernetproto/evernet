package org.evernet.identity.service;

import org.evernet.common.exception.NotFoundException;
import org.evernet.identity.model.KnownEntity;
import org.evernet.identity.pojo.EntityDetails;
import org.evernet.identity.repository.KnownEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Supplier;

@Service
public class KnownEntityService {

    private final KnownEntityRepository knownEntityRepository;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public KnownEntityService(KnownEntityRepository knownEntityRepository, MongoTemplate mongoTemplate) {
        this.knownEntityRepository = knownEntityRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public KnownEntity register(EntityDetails entityDetails) throws Throwable {
        String publicKeyString = entityDetails.getPublicKey().getString();

        mongoTemplate.upsert(Query.query(Criteria.where("publicKey").is(publicKeyString)),
                new Update()
                        .setOnInsert("publicKey", publicKeyString)
                        .setOnInsert("createdOn", new Date())
                        .set("defaultIdentifier", entityDetails.getIdentifier())
                        .addToSet("identifiers", entityDetails.getIdentifier())
                        .set("defaultNode", entityDetails.getNode())
                        .addToSet("nodes", entityDetails.getNode())
                        .set("defaultType", entityDetails.getType())
                        .addToSet("types", entityDetails.getType())
                        .set("defaultDisplayName", entityDetails.getDisplayName())
                        .addToSet("displayNames", entityDetails.getDisplayName())
                        .set("description", entityDetails.getDescription())
                        .set("modifiedOn", new Date()),
                KnownEntity.class);

        return getByPublicKey(publicKeyString);
    }

    private KnownEntity getByPublicKey(String publicKey) throws Throwable {
        return knownEntityRepository.findByPublicKey(publicKey)
                .orElseThrow((Supplier<Throwable>) () -> new NotFoundException("Known entity not found"));
    }
}
