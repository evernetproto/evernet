package org.evernet.app.model;

import org.evernet.common.embedded.DataSchema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "data_formats")
public class DataFormat implements Serializable {

    @Id
    private String id;

    private String identifier;

    private String displayName;

    private String description;

    private DataSchema dataSchema;

    private String appIdentifier; // Allow null for data format definitions without apps

    private String creatorId;

    private Date createdOn;

    private Date modifiedOn;

    public DataFormat() {

    }

    public DataFormat(String identifier, String displayName, String description, DataSchema dataSchema, String appIdentifier, String creatorId) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
        this.dataSchema = dataSchema;
        this.appIdentifier = appIdentifier;
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public DataSchema getDataSchema() {
        return dataSchema;
    }

    public void setDataSchema(DataSchema dataSchema) {
        this.dataSchema = dataSchema;
    }

    public String getAppIdentifier() {
        return appIdentifier;
    }

    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
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
