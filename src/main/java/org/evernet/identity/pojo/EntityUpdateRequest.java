package org.evernet.identity.pojo;

import java.io.Serializable;

public class EntityUpdateRequest implements Serializable {

    private String type;

    private String identifier;

    private String displayName;

    private String description;

    public EntityUpdateRequest() {

    }

    public EntityUpdateRequest(String type, String identifier, String displayName, String description) {
        this.type = type;
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
