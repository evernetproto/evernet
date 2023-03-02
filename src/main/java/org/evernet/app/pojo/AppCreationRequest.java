package org.evernet.app.pojo;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class AppCreationRequest implements Serializable {

    @NotBlank
    private String identifier;

    @NotBlank
    private String displayName;

    private String description;

    public AppCreationRequest() {

    }

    public AppCreationRequest(String identifier, String displayName, String description) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.description = description;
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
