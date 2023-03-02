package org.evernet.acl.model;

import org.evernet.acl.enums.TargetType;
import org.evernet.identity.pojo.EntityDetails;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "accesses")
public class Access implements Serializable {

    @Id
    private String id;

    private TargetType targetType;

    private String targetIdentifier;

    private EntityDetails entity;

    private String creatorId;

    private Date createdOn;

    private Date modifiedOn;

    public Access() {

    }

    public Access(TargetType targetType, String targetIdentifier, EntityDetails entity, String creatorId) {
        this.targetType = targetType;
        this.targetIdentifier = targetIdentifier;
        this.entity = entity;
        this.creatorId = creatorId;
        this.createdOn = new Date();
        this.modifiedOn = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getTargetIdentifier() {
        return targetIdentifier;
    }

    public void setTargetIdentifier(String targetIdentifier) {
        this.targetIdentifier = targetIdentifier;
    }

    public EntityDetails getEntity() {
        return entity;
    }

    public void setEntity(EntityDetails entity) {
        this.entity = entity;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
