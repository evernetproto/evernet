package org.evernet.common.embedded;

import org.evernet.common.enums.DataType;

import java.io.Serializable;

public class FieldSchema implements Serializable {

    private String displayName;

    private String description;

    private DataType dataType;

    private Constraints constraints;

    public FieldSchema() {

    }

    public FieldSchema(String displayName, String description, DataType dataType, Constraints constraints) {
        this.displayName = displayName;
        this.description = description;
        this.dataType = dataType;
        this.constraints = constraints;
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

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Constraints getConstraints() {
        return constraints;
    }

    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }
}
