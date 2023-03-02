package org.evernet.identity.pojo;

import org.evernet.identity.model.Entity;
import org.evernet.identity.model.KnownEntity;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class EntityDetails implements Serializable {

    private String identifier;

    private String node;

    private String type;

    private String displayName;

    private String description;

    private PublicKeyData publicKey;

    public EntityDetails() {
    }

    public EntityDetails(Entity entity, PublicKeyData publicKey, String node) {
        this.identifier = entity.getIdentifier();
        this.node = node;
        this.type = entity.getType();
        this.displayName = entity.getDisplayName();
        this.description = entity.getDescription();
        this.publicKey = publicKey;
    }

    public static EntityDetails fromKnownEntity(KnownEntity knownEntity, String node) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return new EntityDetails(new Entity(
                knownEntity.getDefaultType(),
                knownEntity.getDefaultIdentifier(),
                knownEntity.getDefaultDisplayName(),
                knownEntity.getDescription(),
                null,
                knownEntity.getPublicKey()
        ), new PublicKeyData(
                knownEntity.getPublicKey(),
                node,
                knownEntity.getDefaultIdentifier()
        ), node);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PublicKeyData getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKeyData publicKey) {
        this.publicKey = publicKey;
    }
}
