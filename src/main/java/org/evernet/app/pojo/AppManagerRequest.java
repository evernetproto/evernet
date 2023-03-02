package org.evernet.app.pojo;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class AppManagerRequest implements Serializable {

    @NotBlank
    private String entityIdentifier;

    @NotBlank
    private String entityNode;

    public AppManagerRequest() {

    }

    public AppManagerRequest(String entityIdentifier, String entityNode) {
        this.entityIdentifier = entityIdentifier;
        this.entityNode = entityNode;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public String getEntityNode() {
        return entityNode;
    }

    public void setEntityNode(String entityNode) {
        this.entityNode = entityNode;
    }
}
