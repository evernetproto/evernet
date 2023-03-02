package org.evernet.app.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.evernet.common.embedded.DataSchema;

import java.io.Serializable;

public class DataFormatCreationRequest implements Serializable {

    @NotBlank
    private String identifier;

    @NotBlank
    private String displayName;

    private String description;

    @NotNull
    private DataSchema dataSchema;

    private String appIdentifier;

    public DataFormatCreationRequest() {

    }

    public DataFormatCreationRequest(String identifier, String displayName, String description, DataSchema dataSchema, String appIdentifier) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
        this.dataSchema = dataSchema;
        this.appIdentifier = appIdentifier;
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
}
