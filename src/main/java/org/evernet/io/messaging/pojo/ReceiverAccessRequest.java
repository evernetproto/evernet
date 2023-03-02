package org.evernet.io.messaging.pojo;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class ReceiverAccessRequest implements Serializable {

    @NotBlank
    private String entityIdentifier;

    @NotBlank
    private String entityNode;

    public ReceiverAccessRequest() {

    }

    public ReceiverAccessRequest(String entityIdentifier, String entityNode) {
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
