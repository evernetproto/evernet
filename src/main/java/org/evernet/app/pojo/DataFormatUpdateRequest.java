package org.evernet.app.pojo;

import org.evernet.common.embedded.DataSchema;

import java.io.Serializable;

public class DataFormatUpdateRequest implements Serializable {

    private String displayName;

    private String description;

    private DataSchema dataSchema;

    public DataFormatUpdateRequest() {

    }

    public DataFormatUpdateRequest(String displayName, String description, DataSchema dataSchema) {
        this.displayName = displayName;
        this.description = description;
        this.dataSchema = dataSchema;
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
}
